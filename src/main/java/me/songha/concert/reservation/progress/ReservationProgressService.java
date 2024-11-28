package me.songha.concert.reservation.progress;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.songha.concert.shared.exception.ReservationIllegalArgumentException;
import me.songha.concert.shared.util.ReservationNumberGenerator;
import me.songha.concert.payment.MockPaymentService;
import me.songha.concert.payment.PaymentStatus;
import me.songha.concert.payment.TotalAmountService;
import me.songha.concert.reservation.general.ReservationRepositoryService;
import me.songha.concert.reservation.general.ReservationStatus;
import me.songha.concert.reservation.history.ReservationHistoryRepositoryService;
import me.songha.concert.reservation.preoccupy.PreoccupyRedisService;
import me.songha.concert.reservation.seat.ReservationSeatRepositoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ReservationProgressService {
    private final MockPaymentService mockPaymentService;
    private final ReservationRepositoryService reservationRepositoryService;
    private final ReservationHistoryRepositoryService reservationHistoryRepositoryService;
    private final ReservationSeatRepositoryService reservationSeatRepositoryService;
    private final PreoccupyRedisService preoccupyRedisService;
    private final TotalAmountService totalAmountService;

    public ReservationStatus progressReservation(Long reservationId, Long userId, List<String> seatNumbers, Long concertId) {
        preoccupyRedisService.isSeatValidatedForCurrentUser(concertId, userId, seatNumbers);

        String reservationNumber = ReservationNumberGenerator.generateReservationNumber(reservationId);
        int amount = totalAmountService.getTotalAmount(concertId, seatNumbers);
        PaymentStatus paymentStatus = mockPaymentService.getPaymentStatus(reservationId);

        switch (paymentStatus) {
            case CONFIRMED -> {
                return proceedReservation(ReservationStatus.CONFIRMED, reservationId, userId, seatNumbers, amount, reservationNumber);
            }
            case REJECTED -> {
                return proceedReservation(ReservationStatus.REJECTED, reservationId, userId, seatNumbers, amount, reservationNumber);
            }
            case CANCELED -> {
                return proceedReservation(ReservationStatus.CANCELED, reservationId, userId, seatNumbers, amount, reservationNumber);
            }
            default -> throw new ReservationIllegalArgumentException("[Error] Unexpected value: " + paymentStatus);
        }
    }

    private ReservationStatus proceedReservation(
            ReservationStatus status, Long reservationId,
            Long userId, List<String> seatNumbers, int amount, String reservationNumber) {

        log.info("Reservation processing {}. reservationId={}", status.name(), reservationId);

        if (status.equals(ReservationStatus.CONFIRMED)) {
            reservationSeatRepositoryService.reserveSeats(reservationId, seatNumbers);
        }
        reservationRepositoryService.updateReservationInfo(reservationId, reservationNumber, amount, status);
        reservationHistoryRepositoryService.saveHistory(reservationId, userId, amount, status);

        return status;
    }

}

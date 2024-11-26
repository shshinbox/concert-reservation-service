package me.songha.concert.reservation.history;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.songha.concert.reservation.general.Reservation;
import me.songha.concert.reservation.general.ReservationNotFoundException;
import me.songha.concert.reservation.general.ReservationRepository;
import me.songha.concert.reservation.general.ReservationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ReservationHistoryRepositoryService {
    private final ReservationHistoryRepository reservationHistoryRepository;
    private final ReservationHistoryConverter reservationHistoryConverter;
    private final ReservationRepository reservationRepository;

    public void saveHistory(Long reservationId, Long userId, int amount, ReservationStatus status) {
        try {
            ReservationHistoryDto reservationHistoryDto = ReservationHistoryDto.builder()
                    .reservationId(reservationId)
                    .userId(userId)
                    .amount(amount)
                    .reservationStatus(status.name())
                    .build();
            createReservationHistory(reservationHistoryDto);
        } catch (Exception e) {
            log.error("[Error] Failed to save History. e.getMessage:{}", e.getMessage(), e);
        }
    }

    public ReservationHistoryDto getReservationHistoryById(Long id) {
        return reservationHistoryRepository.findById(id)
                .map(reservationHistoryConverter::toDto)
                .orElseThrow(() -> new ReservationHistoryNotFoundException("[Error] ReservationHistory not found."));
    }

    public ReservationHistoryDto getReservationHistoryById(Long userId, Long reservationId) {
        return reservationHistoryRepository.findByUserIdAndReservationId(userId, reservationId)
                .map(reservationHistoryConverter::toDto)
                .orElseThrow(() -> new ReservationHistoryNotFoundException("[Error] ReservationHistory not found."));
    }

    private void createReservationHistory(ReservationHistoryDto reservationHistoryDto) {
        Reservation reservation = reservationRepository.findById(reservationHistoryDto.getReservationId())
                .orElseThrow(() -> new ReservationNotFoundException("[Error] Reservation not found."));

        ReservationHistory reservationHistory = reservationHistoryConverter.toEntity(reservationHistoryDto, reservation);

        reservationHistoryRepository.save(reservationHistory);
    }

}

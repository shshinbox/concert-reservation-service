package me.songha.concert.reservationhistory;

import me.songha.concert.reservation.general.Reservation;
import me.songha.concert.reservation.general.ReservationNotFoundException;
import me.songha.concert.reservation.general.ReservationRepository;
import me.songha.concert.reservation.history.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ReservationHistoryRepositoryServiceTest {
    @Mock
    private ReservationHistoryRepository reservationHistoryRepository;

    @Mock
    private ReservationHistoryConverter reservationHistoryConverter;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationHistoryRepositoryService reservationHistoryRepositoryService;

    private ReservationHistory reservationHistory;

    private ReservationHistoryDto reservationHistoryDto;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        reservationHistory = ReservationHistory.builder().build();
        reservationHistoryDto = ReservationHistoryDto.builder().reservationId(1L).build();
        reservation = Reservation.builder().id(1L).build();
    }

    @DisplayName("id로 History를 반환한다.")
    @Test
    void getReservationHistoryById() {
        Long historyId = 1L;

        when(reservationHistoryRepository.findById(historyId)).thenReturn(Optional.of(reservationHistory));
        when(reservationHistoryConverter.toDto(reservationHistory)).thenReturn(reservationHistoryDto);

        ReservationHistoryDto result = reservationHistoryRepositoryService.getReservationHistoryById(historyId);

        assertNotNull(result);
        verify(reservationHistoryRepository, times(1)).findById(historyId);
        verify(reservationHistoryConverter, times(1)).toDto(reservationHistory);
    }

    @DisplayName("History를 UserId와 ReservationId로 반환한다.")
    @Test
    void getReservationHistoryByUserIdAndReservationId() {
        Long userId = 1L;
        Long reservationId = 2L;

        when(reservationHistoryRepository.findByUserIdAndReservationId(userId, reservationId))
                .thenReturn(Optional.of(reservationHistory));
        when(reservationHistoryConverter.toDto(reservationHistory)).thenReturn(reservationHistoryDto);

        ReservationHistoryDto result = reservationHistoryRepositoryService.getReservationHistoryById(userId, reservationId);

        assertNotNull(result);
        verify(reservationHistoryRepository, times(1)).findByUserIdAndReservationId(userId, reservationId);
        verify(reservationHistoryConverter, times(1)).toDto(reservationHistory);
    }

    @DisplayName("History를 생성한다.")
    @Test
    void createReservationHistory() {
        Long reservationId = 1L;

        reservationHistoryDto.setReservationId(reservationId);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(reservationHistoryConverter.toEntity(reservationHistoryDto, reservation)).thenReturn(reservationHistory);

//        reservationHistoryRepositoryService.createReservationHistory(reservationHistoryDto);

        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationHistoryConverter, times(1)).toEntity(reservationHistoryDto, reservation);
        verify(reservationHistoryRepository, times(1)).save(reservationHistory);
    }

    @DisplayName("id로 히스토리를 가져올 때 없으면 예외가 발생한다.")
    @Test
    void getReservationHistoryById_NotFound() {
        Long historyId = 1L;

        when(reservationHistoryRepository.findById(historyId)).thenReturn(Optional.empty());

        assertThrows(ReservationHistoryNotFoundException.class, () -> reservationHistoryRepositoryService.getReservationHistoryById(historyId));

        verify(reservationHistoryRepository, times(1)).findById(historyId);
    }

    @DisplayName("History 를 생성할 때 reservation 이 없으면 예외가 발생한다.")
    @Test
    void createReservationHistory_ReservationNotFound() {
        Long reservationId = 1L;

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

//        assertThrows(ReservationNotFoundException.class, () -> reservationHistoryRepositoryService.createReservationHistory(reservationHistoryDto));

        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationHistoryConverter, never()).toEntity(any(), any());
        verify(reservationHistoryRepository, never()).save(any());
    }
}
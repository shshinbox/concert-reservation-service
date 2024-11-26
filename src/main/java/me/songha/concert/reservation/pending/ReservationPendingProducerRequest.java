package me.songha.concert.reservation.pending;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPendingProducerRequest {
    @NotNull
    private Long concertId;
    @NotNull
    private Long userId;
    @NotNull
    @Pattern(regexp = "^RES[0-9]*-[0-9]{14}-[0-9]{3}$")
    private String requestId;
}

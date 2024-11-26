package me.songha.concert.reservation.preoccupy;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PreoccupySeatRequest {
    @NotNull
    private Long concertId;
    @NotNull
    @Pattern(regexp = "^RES[0-9]*-[0-9]{14}-[0-9]{3}$")
    private String requestId;
    @NotNull
    @Size(max = 4)
    private List<String> seatNumbers;
}

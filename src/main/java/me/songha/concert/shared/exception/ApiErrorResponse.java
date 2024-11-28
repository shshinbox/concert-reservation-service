package me.songha.concert.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@AllArgsConstructor
@Data
public class ApiErrorResponse {
    private String message;
    private int httpStatus;
    Map<String, String> errors;
}

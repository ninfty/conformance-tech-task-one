package com.raidiam.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final String error;
    private final String message;
    private final int status;
    private final Instant timestamp;
}

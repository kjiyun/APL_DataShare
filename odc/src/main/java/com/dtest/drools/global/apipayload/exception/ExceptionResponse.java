package com.dtest.drools.global.apipayload.exception;

import com.dtest.drools.global.apipayload.code.status.ErrorStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ExceptionResponse(
        LocalDateTime timestamp,

        String code,
        String message
) {

    public static ExceptionResponse of(ErrorStatus errorStatus) {
        return ExceptionResponse.builder()
                .timestamp(LocalDateTime.now())
                .code(errorStatus.getCode())
                .message(errorStatus.getMessage())
                .build();

    }

    public static ExceptionResponse of(String message) {
        return ExceptionResponse.builder()
                .timestamp(LocalDateTime.now())
                .code(ErrorStatus.INTERNAL_SERVER_ERROR.getCode())
                .message(message)
                .build();

    }
}

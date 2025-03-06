package com.dtest.drools.global.apipayload.code.status;

import com.dtest.drools.global.apipayload.code.BaseCode;
import com.dtest.drools.global.apipayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER NOT FOUND", "유저를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER ALREADY EXIST ", "이미 존재하는 회원입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.NOT_FOUND, "PASSWORD_NOT_MATCH", "비밀번호가 틀렸습니다."),

    TOTP_NOT_MATCH(HttpStatus.NOT_FOUND, "TOTP_NOT_MATCH", "TOTP가 틀렸습니다."),
    CANNOT_READ_CSV(HttpStatus.NOT_FOUND, "CANNOT_READ_CSV", "CSV 파일을 읽을 수 없습니다."),

    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_INVALID", "토큰이 유효하지 않습니다."),

    REDIS_NOT_FOUND(HttpStatus.NOT_FOUND, "REDIS_NOT_FOUND", "Redis를 찾을 수 없습니다."),

    DROOLS_FAIL_RESET(HttpStatus.BAD_REQUEST, "DROOLS_FAIL_RESET", "Drools 초기화를 실패했습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .isSuccess(false)
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .build();
    }
}
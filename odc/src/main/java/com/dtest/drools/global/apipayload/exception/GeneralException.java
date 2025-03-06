package com.dtest.drools.global.apipayload.exception;

import com.dtest.drools.global.apipayload.code.BaseCode;
import com.dtest.drools.global.apipayload.code.ReasonDTO;
import com.dtest.drools.global.apipayload.code.status.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {
//    private BaseCode code;
//
//    public ReasonDTO getErrorReason() {
//        return this.code.getReason();
//    }
private final ErrorStatus errorStatus;

    public GeneralException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }

    public ReasonDTO getErrorStatus() {
        return this.errorStatus.getReason();
    }
}

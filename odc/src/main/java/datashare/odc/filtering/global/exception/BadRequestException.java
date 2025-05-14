package datashare.odc.filtering.global.exception;

import datashare.odc.filtering.global.message.FailureMessage;

public class BadRequestException extends RuntimeException {
    public BadRequestException(FailureMessage failureMessage) {
        super(failureMessage.getMessage());
    }

    public static BadRequestException wrong() {
        return new BadRequestException(FailureMessage.BAD_REQUEST);
    }
}

package datashare.odc.filtering.global.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FailureMessage {
    /* 400 Bad Request */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "요청 형식이 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}

package apl.filtering.frame;

import apl.filtering.global.exception.BadRequestException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Attribute {
    /* modifiable start */
    INDEX("진료 기록 ID"),
    SBP("수축기 혈압"),
    RESPIRATION_RATE("호흡률"),
    COMPLAINTS("호소 증상"),
    HEART_RATE("심장 박동수"),
    OXYGEN_SATURATION("산소 포화도");
    /* modifiable end */

    private final String alias;

    Attribute(String alias) {
        this.alias = alias;
    }

    public static Attribute from(String input) {
        return Arrays.stream(Attribute.values())
                .filter(attribute -> attribute.alias.equals(input))
                .findFirst()
                .orElseThrow(BadRequestException::wrong);
    }
}

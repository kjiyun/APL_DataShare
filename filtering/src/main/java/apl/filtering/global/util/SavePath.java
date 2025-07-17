package apl.filtering.global.util;

import lombok.Getter;

@Getter
public enum SavePath {
    ORIGINAL("/app/data/original_data.csv"),
    FILTERED("/app/data/filtered_data.csv"),
    FRAME("/app/filtering/src/main/java/apl/filtering/frame"),
    RULE("/app/filtering/src/main/resources/rules"),
    JSON("/app/data/mappingInfo.json"),
    NEW_BUILD("/filtering");

    private final String path;

    SavePath(String path) {
        this.path = path;
    }
}

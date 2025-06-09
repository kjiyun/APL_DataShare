package apl.filtering.frame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record DataRecord
        (
                Long index,
                Double heartRate,
                Double oxygenSaturation,
                Double sbp,
                Double respirationRate,
                String complaints,
                List<String> attributes
        ) {
    public static DataRecord from(Map<Attribute, String> info) {
        return DataRecord.builder()
                /* modifiable start */
                .index(Long.parseLong(info.get(Attribute.INDEX)))
                .heartRate(Double.parseDouble(info.get(Attribute.HEART_RATE)))
                .oxygenSaturation(Double.parseDouble(info.get(Attribute.OXYGEN_SATURATION)))
                .sbp(Double.parseDouble(info.get(Attribute.SBP)))
                .respirationRate(Double.parseDouble(info.get(Attribute.RESPIRATION_RATE)))
                .complaints(info.get(Attribute.COMPLAINTS).toLowerCase())
                /* modifiable end */
                .attributes(info.values().stream().toList())
                .build();
    }

    public List<String> getAttributes() {
        return new ArrayList<>(attributes);
    }
}
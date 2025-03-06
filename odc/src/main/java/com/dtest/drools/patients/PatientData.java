package com.dtest.drools.patients;

import com.dtest.drools.global.core.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

//@Entity
@Getter
@Setter
public class PatientData extends BaseEntity {

//    private Long id;
    private int heartRate;
    private int oxygenSaturation;

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public double getOxygenSaturation() {
        return oxygenSaturation;
    }

    public void setOxygenSaturation(int oxygenSaturation) {
        this.oxygenSaturation = oxygenSaturation;
    }

}

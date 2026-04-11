package edu.cit.panugaling.motomeet.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BikeForm {
    @NotBlank
    private String displayName;

    @NotNull
    @Min(1950)
    @Max(2100)
    private Integer modelYear;

    @NotNull
    @Min(50)
    private Integer engineCc;

    @NotNull
    @Min(1)
    private Integer powerHp;

    @NotNull
    @Min(1)
    private Integer weightKg;

    @NotNull
    @Min(20)
    private Integer topSpeedKph;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getModelYear() {
        return modelYear;
    }

    public void setModelYear(Integer modelYear) {
        this.modelYear = modelYear;
    }

    public Integer getEngineCc() {
        return engineCc;
    }

    public void setEngineCc(Integer engineCc) {
        this.engineCc = engineCc;
    }

    public Integer getPowerHp() {
        return powerHp;
    }

    public void setPowerHp(Integer powerHp) {
        this.powerHp = powerHp;
    }

    public Integer getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Integer weightKg) {
        this.weightKg = weightKg;
    }

    public Integer getTopSpeedKph() {
        return topSpeedKph;
    }

    public void setTopSpeedKph(Integer topSpeedKph) {
        this.topSpeedKph = topSpeedKph;
    }
}

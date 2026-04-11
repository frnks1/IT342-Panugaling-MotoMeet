package edu.cit.panugaling.motomeet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "bikes")
public class Bike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String displayName;

    @NotNull
    @Min(1950)
    @Max(2100)
    @Column(nullable = false)
    private Integer modelYear;

    @NotNull
    @Min(50)
    @Column(nullable = false)
    private Integer engineCc;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer powerHp;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer weightKg;

    @NotNull
    @Min(20)
    @Column(nullable = false)
    private Integer topSpeedKph;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    public Bike() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}

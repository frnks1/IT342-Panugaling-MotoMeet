package edu.cit.panugaling.motomeet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class RideForm {
    @NotBlank
    private String title;

    @NotBlank
    private String route;

    @NotNull
    @Min(1)
    private Integer distanceMiles;

    @NotNull
    @Min(1)
    private Integer durationMinutes;

    @NotNull
    @Min(1)
    private Integer avgSpeedMph;

    @NotNull
    private LocalDate rideDate;

    private String imageUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public Integer getDistanceMiles() {
        return distanceMiles;
    }

    public void setDistanceMiles(Integer distanceMiles) {
        this.distanceMiles = distanceMiles;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getAvgSpeedMph() {
        return avgSpeedMph;
    }

    public void setAvgSpeedMph(Integer avgSpeedMph) {
        this.avgSpeedMph = avgSpeedMph;
    }

    public LocalDate getRideDate() {
        return rideDate;
    }

    public void setRideDate(LocalDate rideDate) {
        this.rideDate = rideDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

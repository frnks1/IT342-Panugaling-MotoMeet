package edu.cit.panugaling.motomeet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "ride_logs")
public class RideLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String route;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer distanceMiles;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer durationMinutes;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer avgSpeedMph;

    @NotNull
    @Column(nullable = false)
    private LocalDate rideDate;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public RideLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}

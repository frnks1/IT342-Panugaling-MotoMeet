package edu.cit.panugaling.motomeet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "meetups")
public class Meetup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = true)
    private String title;

    @NotBlank
    @Column(nullable = true)
    private String location;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private Integer maxParticipants;

    @Column(nullable = true)
    private String organizerName;

    @Column(nullable = true)
    private LocalDateTime scheduledAt;

    @Column(nullable = true)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDate meetupDate;

    @Column(nullable = true)
    private LocalTime meetupTime;

    @NotNull
    @DecimalMin("0.1")
    @Column(nullable = true)
    private Double distanceMiles;

    @Column(nullable = true)
    private String imageUrl;

    @NotNull
    @Column(nullable = true)
    private Integer goingCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    public Meetup() {
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getMeetupDate() {
        return meetupDate;
    }

    public void setMeetupDate(LocalDate meetupDate) {
        this.meetupDate = meetupDate;
    }

    public LocalTime getMeetupTime() {
        return meetupTime;
    }

    public void setMeetupTime(LocalTime meetupTime) {
        this.meetupTime = meetupTime;
    }

    public Double getDistanceMiles() {
        return distanceMiles;
    }

    public void setDistanceMiles(Double distanceMiles) {
        this.distanceMiles = distanceMiles;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getGoingCount() {
        return goingCount;
    }

    public void setGoingCount(Integer goingCount) {
        this.goingCount = goingCount;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}

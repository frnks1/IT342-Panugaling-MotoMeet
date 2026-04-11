package edu.cit.panugaling.motomeet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "meetups")
public class Meetup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String location;

    @NotNull
    @Column(nullable = false)
    private LocalDate meetupDate;

    @NotNull
    @Column(nullable = false)
    private LocalTime meetupTime;

    @NotNull
    @DecimalMin("0.1")
    @Column(nullable = false)
    private Double distanceMiles;

    @Column(nullable = false)
    private String imageUrl;

    @NotNull
    @Column(nullable = false)
    private Integer goingCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
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

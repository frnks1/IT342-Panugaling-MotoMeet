package edu.cit.panugaling.motomeet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "feed_posts")
public class FeedPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 1000)
    private String story;

    @NotBlank
    @Column(nullable = false)
    private String bikeName;

    @NotBlank
    @Column(nullable = false)
    private String imageLeftUrl;

    @NotBlank
    @Column(nullable = false)
    private String imageRightUrl;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer likes;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer cheers;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer comments;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    public FeedPost() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getBikeName() {
        return bikeName;
    }

    public void setBikeName(String bikeName) {
        this.bikeName = bikeName;
    }

    public String getImageLeftUrl() {
        return imageLeftUrl;
    }

    public void setImageLeftUrl(String imageLeftUrl) {
        this.imageLeftUrl = imageLeftUrl;
    }

    public String getImageRightUrl() {
        return imageRightUrl;
    }

    public void setImageRightUrl(String imageRightUrl) {
        this.imageRightUrl = imageRightUrl;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getCheers() {
        return cheers;
    }

    public void setCheers(Integer cheers) {
        this.cheers = cheers;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}

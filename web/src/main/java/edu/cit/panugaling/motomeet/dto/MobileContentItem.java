package edu.cit.panugaling.motomeet.dto;

public class MobileContentItem {
    private Long id;
    private String section;
    private String title;
    private String subtitle;
    private String description;
    private String imageUrl;
    private String secondaryImageUrl;
    private String metaLeft;
    private String metaRight;
    private String badge;
    private String timestamp;

    public MobileContentItem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSecondaryImageUrl() {
        return secondaryImageUrl;
    }

    public void setSecondaryImageUrl(String secondaryImageUrl) {
        this.secondaryImageUrl = secondaryImageUrl;
    }

    public String getMetaLeft() {
        return metaLeft;
    }

    public void setMetaLeft(String metaLeft) {
        this.metaLeft = metaLeft;
    }

    public String getMetaRight() {
        return metaRight;
    }

    public void setMetaRight(String metaRight) {
        this.metaRight = metaRight;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
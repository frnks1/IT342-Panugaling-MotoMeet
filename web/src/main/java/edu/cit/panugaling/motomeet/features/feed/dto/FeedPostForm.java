package edu.cit.panugaling.motomeet.features.feed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FeedPostForm {
    @NotBlank
    @Size(max = 1000)
    private String story;

    private String bikeName;
    private String imageLeftUrl;
    private String imageRightUrl;

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
}

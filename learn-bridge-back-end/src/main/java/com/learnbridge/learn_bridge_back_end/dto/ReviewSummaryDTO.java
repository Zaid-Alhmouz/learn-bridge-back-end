package com.learnbridge.learn_bridge_back_end.dto;

public class ReviewSummaryDTO {

    private String reviewerName;
    private Integer stars;
    private String description;

    public ReviewSummaryDTO() { }

    public ReviewSummaryDTO(String reviewerName, Integer stars, String description) {
        this.reviewerName = reviewerName;
        this.stars = stars;
        this.description = description;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ReviewSummaryDTO{" +
                "reviewerName='" + reviewerName + '\'' +
                ", stars=" + stars +
                ", description='" + description + '\'' +
                '}';
    }
}

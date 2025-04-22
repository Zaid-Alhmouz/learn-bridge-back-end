package com.learnbridge.learn_bridge_back_end.dto;

import com.learnbridge.learn_bridge_back_end.entity.Rating;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReviewDTO {

    private Long ratingId;
    private Long sessionId;
    private Long learnerId;
    private Long instructorId;
    private String description;
    private int stars;
    private LocalDate reviewDate;

    public ReviewDTO() {}

    public ReviewDTO(Rating rating) {
        this.ratingId = rating.getRatingId();
        this.sessionId = rating.getSession().getSessionId();
        this.learnerId = rating.getLearner().getLearnerId();
        this.instructorId = rating.getInstructor().getInstructorId();
        this.description = rating.getDescription();
        this.stars = rating.getStars();
        this.reviewDate = LocalDate.now();
    }

    public Long getRatingId() {
        return ratingId;
    }

    public void setRatingId(Long ratingId) {
        this.ratingId = ratingId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getLearnerId() {
        return learnerId;
    }

    public void setLearnerId(Long learnerId) {
        this.learnerId = learnerId;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    @Override
    public String toString() {
        return "ReviewDTO{" +
                "ratingId=" + ratingId +
                ", sessionId=" + sessionId +
                ", learnerId=" + learnerId +
                ", instructorId=" + instructorId +
                ", description='" + description + '\'' +
                ", stars=" + stars +
                ", reviewDate=" + reviewDate +
                '}';
    }
}

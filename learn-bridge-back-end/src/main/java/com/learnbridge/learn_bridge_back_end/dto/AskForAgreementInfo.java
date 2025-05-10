package com.learnbridge.learn_bridge_back_end.dto;

import java.math.BigDecimal;

public class AskForAgreementInfo {

    private Long agreementId;
    private Long instructorId;
    private Long postId;
    private Long learnerId;
    private Long sessionsNumber;
    private Long ratingsNumber;
    private BigDecimal averageRating;
    private String category;
    private String subject;
    private String description;
    private String learnerName;
    private String instructorName;
    private String instructorBio;
    private BigDecimal price;

    public AskForAgreementInfo() {}


    public void setAgreementId(Long agreementId) {
        this.agreementId = agreementId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public void setLearnerId(Long learnerId) {
        this.learnerId = learnerId;
    }

    public Long getAgreementId() {
        return agreementId;
    }



    public Long getInstructorId() {
        return instructorId;
    }



    public Long getPostId() {
        return postId;
    }



    public Long getLearnerId() {
        return learnerId;
    }



    public Long getSessionsNumber() {
        return sessionsNumber;
    }

    public void setSessionsNumber(Long sessionsNumber) {
        this.sessionsNumber = sessionsNumber;
    }

    public Long getRatingsNumber() {
        return ratingsNumber;
    }

    public void setRatingsNumber(Long ratingsNumber) {
        this.ratingsNumber = ratingsNumber;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLearnerName() {
        return learnerName;
    }

    public void setLearnerName(String learnerName) {
        this.learnerName = learnerName;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getInstructorBio() {
        return instructorBio;
    }

    public void setInstructorBio(String instructorBio) {
        this.instructorBio = instructorBio;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "AskForAgreementInfo{" +
                "agreementId=" + agreementId +
                ", instructorId=" + instructorId +
                ", postId=" + postId +
                ", learnerId=" + learnerId +
                ", sessionsNumber=" + sessionsNumber +
                ", ratingsNumber=" + ratingsNumber +
                ", averageRating=" + averageRating +
                ", category='" + category + '\'' +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", learnerName='" + learnerName + '\'' +
                ", instructorName='" + instructorName + '\'' +
                ", instructorBio='" + instructorBio + '\'' +
                ", price=" + price +
                '}';
    }
}

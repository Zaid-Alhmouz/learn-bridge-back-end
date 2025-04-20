package com.learnbridge.learn_bridge_back_end.dto;

public class AgreementResponseDTO {

    private Long agreementId;
    private String instructorName;
    private String learnerName;
    private String postSSubject;
    private String postStatus;

    public AgreementResponseDTO() {}

    public Long getAgreementId() {
        return agreementId;
    }

    public void setAgreementId(Long agreementId) {
        this.agreementId = agreementId;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getLearnerName() {
        return learnerName;
    }

    public void setLearnerName(String learnerName) {
        this.learnerName = learnerName;
    }

    public String getPostSSubject() {
        return postSSubject;
    }

    public void setPostSSubject(String postSSubject) {
        this.postSSubject = postSSubject;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }

    @Override
    public String toString() {
        return "AgreementResponseDTO{" +
                "agreementId=" + agreementId +
                ", instructorName='" + instructorName + '\'' +
                ", learnerName='" + learnerName + '\'' +
                ", postSSubject='" + postSSubject + '\'' +
                ", postStatus='" + postStatus + '\'' +
                '}';
    }
}

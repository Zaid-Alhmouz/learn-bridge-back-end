package com.learnbridge.learn_bridge_back_end.dto;

import com.learnbridge.learn_bridge_back_end.entity.SessionStatus;

public class SessionDTO {

    private Long sessionId;
    private Long instructorId;
    private Long agreementId;
    private String sessionStatus;

    public SessionDTO() {}

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }

    public Long getAgreementId() {
        return agreementId;
    }

    public void setAgreementId(Long agreementId) {
        this.agreementId = agreementId;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    @Override
    public String toString() {
        return "SessionDTO{" +
                "sessionId=" + sessionId +
                ", instructorId=" + instructorId +
                ", agreementId=" + agreementId +
                ", sessionStatus='" + sessionStatus + '\'' +
                '}';
    }
}

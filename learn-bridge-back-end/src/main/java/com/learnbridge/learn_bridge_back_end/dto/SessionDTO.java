package com.learnbridge.learn_bridge_back_end.dto;

import com.learnbridge.learn_bridge_back_end.entity.SessionStatus;

public class SessionDTO {

    private Long sessionId;
    private Long transactionId;
    private SessionStatus sessionStatus;

    public SessionDTO() {}

    public SessionDTO(Long sessionId, Long transactionId, SessionStatus sessionStatus) {
        this.sessionId = sessionId;
        this.transactionId = transactionId;
        this.sessionStatus = sessionStatus;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public SessionStatus getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(SessionStatus sessionStatus) {
        this.sessionStatus = sessionStatus;
    }
}

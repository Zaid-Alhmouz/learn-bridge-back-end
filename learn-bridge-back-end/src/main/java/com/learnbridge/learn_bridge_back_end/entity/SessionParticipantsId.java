package com.learnbridge.learn_bridge_back_end.entity;

import java.io.Serializable;
import java.util.Objects;

public class SessionParticipantsId implements Serializable {

    private Long sessionId;
    private Long learnerId;

    public SessionParticipantsId() {
    }

    public SessionParticipantsId(Long sessionId, Long learnerId) {
        this.sessionId = sessionId;
        this.learnerId = learnerId;
    }

    // Getters and Setters

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

    // Override equals and hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SessionParticipantsId that = (SessionParticipantsId) o;

        if (!Objects.equals(sessionId, that.sessionId)) return false;
        return Objects.equals(learnerId, that.learnerId);
    }

    @Override
    public int hashCode() {
        int result = sessionId != null ? sessionId.hashCode() : 0;
        result = 31 * result + (learnerId != null ? learnerId.hashCode() : 0);
        return result;
    }
}
package com.learnbridge.learn_bridge_back_end.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;


public class ParticipatedLearnerId implements Serializable {
    private Long chatId;
    private Long learnerId;


    public ParticipatedLearnerId() {}

    public ParticipatedLearnerId(Long chatId, Long learnerId) {
        this.chatId = chatId;
        this.learnerId = learnerId;
    }

    public Long getChatId() { return chatId; }
    public Long getLearnerId() { return learnerId; }


    public void setChatId(Long chatId) { this.chatId = chatId; }
    public void setLearnerId(Long learnerId) { this.learnerId = learnerId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipatedLearnerId that = (ParticipatedLearnerId) o;
        return Objects.equals(chatId, that.chatId) &&
                Objects.equals(learnerId, that.learnerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, learnerId);
    }
}
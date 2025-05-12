package com.learnbridge.learn_bridge_back_end.dto;

import com.learnbridge.learn_bridge_back_end.entity.SessionStatus;

public class ChatSummaryDTO {
    private Long chatId;
    private Long sessionId;
    private String participantName;   // the “other” party’s full name
    private SessionStatus sessionStatus;
    public ChatSummaryDTO() { }

    public ChatSummaryDTO(Long chatId, Long sessionId, String participantName, SessionStatus sessionStatus) {
        this.chatId = chatId;
        this.sessionId = sessionId;
        this.participantName = participantName;
        this.sessionStatus = sessionStatus;
    }

    public Long getChatId() {
        return chatId;
    }
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getSessionId() {
        return sessionId;
    }
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getParticipantName() {
        return participantName;
    }
    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public SessionStatus getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(SessionStatus sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    @Override
    public String toString() {
        return "ChatSummaryDTO{" +
                "chatId=" + chatId +
                ", sessionId=" + sessionId +
                ", participantName='" + participantName + '\'' +
                ", sessionStatus=" + sessionStatus +
                '}';
    }
}

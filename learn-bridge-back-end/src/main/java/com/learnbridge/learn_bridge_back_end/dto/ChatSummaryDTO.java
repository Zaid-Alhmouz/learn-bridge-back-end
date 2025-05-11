package com.learnbridge.learn_bridge_back_end.dto;

public class ChatSummaryDTO {
    private Long chatId;
    private Long sessionId;
    private String participantName;   // the “other” party’s full name

    public ChatSummaryDTO() { }

    public ChatSummaryDTO(Long chatId, Long sessionId, String participantName) {
        this.chatId = chatId;
        this.sessionId = sessionId;
        this.participantName = participantName;
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
}

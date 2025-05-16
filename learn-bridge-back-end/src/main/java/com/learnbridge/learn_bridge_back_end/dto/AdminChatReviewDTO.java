package com.learnbridge.learn_bridge_back_end.dto;

import java.util.List;

public class AdminChatReviewDTO {
    private Long chatId;
    private Long sessionId;
    private String sessionStatus;

    private Long instructorId;
    private String instructorName;

    private Long learnerId;
    private String learnerName;

    private List<MessageDTO> messages;
    private List<FileDTO> files;

    public AdminChatReviewDTO() {}

    public AdminChatReviewDTO(Long chatId,
                              Long sessionId,
                              String sessionStatus,
                              Long instructorId,
                              String instructorName,
                              Long learnerId,
                              String learnerName,
                              List<MessageDTO> messages,
                              List<FileDTO> files) {
        this.chatId = chatId;
        this.sessionId = sessionId;
        this.sessionStatus = sessionStatus;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
        this.learnerId = learnerId;
        this.learnerName = learnerName;
        this.messages = messages;
        this.files = files;
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

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public Long getLearnerId() {
        return learnerId;
    }

    public void setLearnerId(Long learnerId) {
        this.learnerId = learnerId;
    }

    public String getLearnerName() {
        return learnerName;
    }

    public void setLearnerName(String learnerName) {
        this.learnerName = learnerName;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }

    public List<FileDTO> getFiles() {
        return files;
    }

    public void setFiles(List<FileDTO> files) {
        this.files = files;
    }

    @Override
    public String toString() {
        return "AdminChatReviewDTO{" +
                "chatId=" + chatId +
                ", sessionId=" + sessionId +
                ", sessionStatus='" + sessionStatus + '\'' +
                ", instructorId=" + instructorId +
                ", instructorName='" + instructorName + '\'' +
                ", learnerId=" + learnerId +
                ", learnerName='" + learnerName + '\'' +
                ", messages=" + messages +
                ", files=" + files +
                '}';
    }
}

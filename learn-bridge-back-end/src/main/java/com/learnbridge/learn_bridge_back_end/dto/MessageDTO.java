package com.learnbridge.learn_bridge_back_end.dto;

import com.learnbridge.learn_bridge_back_end.entity.TextMessage;

import java.time.LocalDateTime;

public class MessageDTO {

    private Long messageId;
    private Long senderId;
    private LocalDateTime sentAt;
    private String content;
    private String senderName;

    public MessageDTO() {}

    public MessageDTO(TextMessage textMessage) {
        this.messageId = textMessage.getMessageId();
        this.senderId = textMessage.getSender().getId();
        this.sentAt = LocalDateTime.now();
        this.content = textMessage.getContent();
        this.senderName = textMessage.getSenderName();
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "messageId=" + messageId +
                ", senderId=" + senderId +
                ", sentAt=" + sentAt +
                ", content='" + content + '\'' +
                ", senderName='" + senderName + '\'' +
                '}';
    }
}

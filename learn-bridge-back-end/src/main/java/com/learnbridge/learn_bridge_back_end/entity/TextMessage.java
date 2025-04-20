package com.learnbridge.learn_bridge_back_end.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "text_message")
public class TextMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", nullable = false, updatable = false)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Column(name = "sent_at")
    private LocalDate sentAt;

    @Column(name = "content", length = 255)
    private String content;


    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public LocalDate getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDate sentAt) {
        this.sentAt = sentAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "TextMessage{" +
                "messageId=" + messageId +
                ", sender=" + sender +
                ", chat=" + chat +
                ", sentAt=" + sentAt +
                ", content='" + content + '\'' +
                '}';
    }
}

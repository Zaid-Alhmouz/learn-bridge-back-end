package com.learnbridge.learn_bridge_back_end.entity;


import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id", nullable = false, updatable = false)
    private Long chatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TextMessage> messages;


    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Set<TextMessage> getMessages() {
        return messages;
    }

    public void setMessages(Set<TextMessage> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "chatId=" + chatId +
                ", session=" + session +
                ", messages=" + messages +
                '}';
    }
}

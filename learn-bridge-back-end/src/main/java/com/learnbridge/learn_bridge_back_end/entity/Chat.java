package com.learnbridge.learn_bridge_back_end.entity;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", referencedColumnName = "session_id")
    private Session session;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", referencedColumnName = "user_id")
    private User instructor;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "agreement_id", referencedColumnName = "agreement_id")
    private Agreement agreement;

    @OneToMany(mappedBy = "chat")
    private List<ParticipatedLearners> participants;

    public Long getChatId() {
        return chatId;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public User getInstructor() {
        return instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }


    @Override
    public String toString() {
        return "Chat{" +
                "chatId=" + chatId +
                ", session=" + session +
                ", instructor=" + instructor +
                '}';
    }
}

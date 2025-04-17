package com.learnbridge.learn_bridge_back_end.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "participated_learners")
@IdClass(ParticipatedLearnerId.class)
public class ParticipatedLearners {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Id
    @Column(name = "learner_id")
    private Long learnerId;


    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatId")
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("learnerId")
    @JoinColumn(name = "learner_id", nullable = false)
    private Learner learner;



    public Chat getChat() { return chat; }
    public Learner getLearner() { return learner; }
    public void setChat(Chat chat) { this.chat = chat; }
    public void setLearner(Learner learner) { this.learner = learner; }
}
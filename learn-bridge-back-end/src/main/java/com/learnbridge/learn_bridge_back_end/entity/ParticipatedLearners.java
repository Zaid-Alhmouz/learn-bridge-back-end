package com.learnbridge.learn_bridge_back_end.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "participated_learners")
public class ParticipatedLearners {

    @EmbeddedId
    private ParticipatedLearnerId participatedLearnerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatId")
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("learnerId")
    @JoinColumn(name = "learner_id", nullable = false)
    private Learner learner;


    public ParticipatedLearners() {}

    public ParticipatedLearners(Chat chat, Learner learner) {
        this.chat = chat;
        this.learner = learner;
        this.participatedLearnerId = new ParticipatedLearnerId(chat.getChatId(), learner.getLearnerId());
    }


    public ParticipatedLearnerId getId() { return participatedLearnerId; }
    public Chat getChat() { return chat; }
    public Learner getLearner() { return learner; }
    public void setChat(Chat chat) { this.chat = chat; }
    public void setLearner(Learner learner) { this.learner = learner; }
}
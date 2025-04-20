package com.learnbridge.learn_bridge_back_end.entity;

import com.learnbridge.learn_bridge_back_end.entity.Learner;
import com.learnbridge.learn_bridge_back_end.entity.SessionParticipantsId;
import com.learnbridge.learn_bridge_back_end.entity.Session;
import jakarta.persistence.*;


@Entity
@Table(name = "session_participants")
@IdClass(SessionParticipantsId.class)
public class SessionParticipants {

    @Id
    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Id
    @Column(name = "learner_id", nullable = false)
    private Long learnerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learner_id", referencedColumnName = "learner_id", insertable = false, updatable = false)
    private Learner learner;

    // Constructors

    public SessionParticipants() {
    }

    public SessionParticipants(Long sessionId, Long learnerId) {
        this.sessionId = sessionId;
        this.learnerId = learnerId;
    }

    // Getters and Setters

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getLearnerId() {
        return learnerId;
    }

    public void setLearnerId(Long learnerId) {
        this.learnerId = learnerId;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Learner getLearner() {
        return learner;
    }

    public void setLearner(Learner learner) {
        this.learner = learner;
    }
}
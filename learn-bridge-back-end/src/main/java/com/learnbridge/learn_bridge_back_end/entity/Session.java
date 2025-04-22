package com.learnbridge.learn_bridge_back_end.entity;

import com.learnbridge.learn_bridge_back_end.converter.CaseInsensitiveEnumConverter;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id", nullable = false, updatable = false)
    private Long sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private PaymentInfo transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agreement_id", nullable = false)
    private Agreement agreement;

    @Convert(converter = CaseInsensitiveEnumConverter.class)
    @Column(name = "session_status")
    private SessionStatus sessionStatus;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Chat> chats;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SessionParticipants> participants;

    @Column(name = "cancelled_by_id")
    private Long cancelledById;

    @Column(name = "finished_by_id")
    private Long finishedById;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public PaymentInfo getTransaction() {
        return transaction;
    }

    public void setTransaction(PaymentInfo transaction) {
        this.transaction = transaction;
    }

    public User getInstructor() {
        return instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }

    public Agreement getAgreement() {
        return agreement;
    }

    public void setAgreement(Agreement agreement) {
        this.agreement = agreement;
    }

    public SessionStatus getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(SessionStatus sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public Set<Chat> getChats() {
        return chats;
    }

    public void setChats(Set<Chat> chats) {
        this.chats = chats;
    }

    public Set<SessionParticipants> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<SessionParticipants> participants) {
        this.participants = participants;
    }


    public Long getCancelledById() {
        return cancelledById;
    }

    public void setCancelledById(Long cancelledById) {
        this.cancelledById = cancelledById;
    }

    public Long getFinishedById() {
        return finishedById;
    }

    public void setFinishedById(Long finishedById) {
        this.finishedById = finishedById;
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionId=" + sessionId +
                ", transaction=" + transaction +
                ", instructor=" + instructor +
                ", agreement=" + agreement +
                ", sessionStatus=" + sessionStatus +
                ", chats=" + chats +
                ", participants=" + participants +
                '}';
    }
}

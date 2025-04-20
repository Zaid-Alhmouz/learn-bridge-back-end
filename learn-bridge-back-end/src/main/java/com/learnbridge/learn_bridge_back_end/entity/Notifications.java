package com.learnbridge.learn_bridge_back_end.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agreement_id", referencedColumnName = "agreement_id")
    private Agreement agreement;

    @Enumerated(EnumType.STRING)
    @Column(name = "read_status")
    private ReadStatus readStatus;

    @Column(name = "notification_type")
    private String notificationType;

    @Column(name = "time_stamp")
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    public Long getNotificationId() {
        return notificationId;
    }



    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ReadStatus getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(ReadStatus readStatus) {
        this.readStatus = readStatus;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Agreement getAgreement() {
        return agreement;
    }

    public void setAgreement(Agreement agreement) {
        this.agreement = agreement;
    }

    @Override
    public String toString() {
        return "Notifications{" +
                "notificationId=" + notificationId +
                ", user=" + user +
                ", readStatus=" + readStatus +
                ", notificationType='" + notificationType + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}


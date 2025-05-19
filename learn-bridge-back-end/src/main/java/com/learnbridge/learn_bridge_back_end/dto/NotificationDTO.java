package com.learnbridge.learn_bridge_back_end.dto;

import com.learnbridge.learn_bridge_back_end.entity.NotificationType;

import java.time.LocalDateTime;

public class NotificationDTO {

    private Long notificationId;
    private Long userId;
    private Long agreementId;
    private NotificationType notificationType;
    private String readStatus;
    private LocalDateTime timestamp;
    private String message;

    public NotificationDTO() {}

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAgreementId() {
        return agreementId;
    }

    public void setAgreementId(Long agreementId) {
        this.agreementId = agreementId;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "NotificationDTO{" +
                "notificationId=" + notificationId +
                ", userId=" + userId +
                ", agreementId=" + agreementId +
                ", notificationType='" + notificationType + '\'' +
                ", readStatus='" + readStatus + '\'' +
                ", timestamp=" + timestamp +
                ", message='" + message + '\'' +
                '}';
    }
}

package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.NotificationsDAO;
import com.learnbridge.learn_bridge_back_end.entity.*;
import com.learnbridge.learn_bridge_back_end.dto.NotificationDTO;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationsDAO notificationsDAO;

    /**
     * Create notification when an instructor requests an agreement
     */
    @Transactional
    public Notifications createAgreementRequestNotification(Agreement agreement) {
        Notifications notification = new Notifications();
        notification.setUser(agreement.getLearner().getUser());
        notification.setAgreement(agreement);
        notification.setNotificationType("AGREEMENT_REQUEST");
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(String.format(
                "Instructor %s %s requested to tutor on your post '%s'.",
                agreement.getInstructor().getFirstName(),
                agreement.getInstructor().getLastName(),
                agreement.getPost() != null ? agreement.getPost().getSubject() : "N/A"
        ));
        return notificationsDAO.saveNotification(notification);
    }

    /**
     * Create notification when a learner requests an agreement
     */
    @Transactional
    public Notifications createLearnerRequestNotification(Agreement agreement) {
        Notifications notification = new Notifications();
        notification.setUser(agreement.getInstructor().getUser());
        notification.setAgreement(agreement);
        notification.setNotificationType("LEARNER_REQUEST");
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(String.format(
                "Learner %s %s requested your tutoring services at $%s.",
                agreement.getLearner().getFirstName(),
                agreement.getLearner().getLastName(),
                agreement.getPrice()
        ));
        return notificationsDAO.saveNotification(notification);
    }

    /**
     * Notification when an agreement is accepted by learner
     */
    @Transactional
    public Notifications createAgreementAcceptedNotification(Agreement agreement) {
        Notifications notification = new Notifications();
        notification.setUser(agreement.getInstructor().getUser());
        notification.setAgreement(agreement);
        notification.setNotificationType("AGREEMENT_ACCEPTED");
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(String.format(
                "Learner %s %s accepted your agreement for '%s'.",
                agreement.getLearner().getFirstName(),
                agreement.getLearner().getLastName(),
                agreement.getPost() != null ? agreement.getPost().getSubject() : "N/A"
        ));
        return notificationsDAO.saveNotification(notification);
    }

    /**
     * Notification when an agreement is rejected by learner
     */
    @Transactional
    public Notifications createAgreementRejectedNotification(Agreement agreement) {
        Notifications notification = new Notifications();
        notification.setUser(agreement.getInstructor().getUser());
        notification.setAgreement(agreement);
        notification.setNotificationType("AGREEMENT_REJECTED");
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(String.format(
                "Learner %s %s rejected your agreement for '%s'.",
                agreement.getLearner().getFirstName(),
                agreement.getLearner().getLastName(),
                agreement.getPost() != null ? agreement.getPost().getSubject() : "N/A"
        ));
        return notificationsDAO.saveNotification(notification);
    }

    /**
     * Notification when learner-initiated request is accepted by instructor
     */
    @Transactional
    public Notifications createLearnerRequestAcceptedNotification(Agreement agreement) {
        Notifications notification = new Notifications();
        notification.setUser(agreement.getLearner().getUser());
        notification.setAgreement(agreement);
        notification.setNotificationType("LEARNER_REQUEST_ACCEPTED");
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(String.format(
                "Instructor %s %s accepted your tutoring request.",
                agreement.getInstructor().getFirstName(),
                agreement.getInstructor().getLastName()
        ));
        return notificationsDAO.saveNotification(notification);
    }

    /**
     * Notification when learner-initiated request is rejected
     */
    @Transactional
    public Notifications createLearnerRequestRejectedNotification(Agreement agreement) {
        Notifications notification = new Notifications();
        notification.setUser(agreement.getLearner().getUser());
        notification.setAgreement(agreement);
        notification.setNotificationType("LEARNER_REQUEST_REJECTED");
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(String.format(
                "Instructor %s %s declined your tutoring request.",
                agreement.getInstructor().getFirstName(),
                agreement.getInstructor().getLastName()
        ));
        return notificationsDAO.saveNotification(notification);
    }

    /**
     * Common: mark a notification as read
     */
    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        Notifications notification = notificationsDAO.findNotificationById(notificationId);
        if (notification != null) {
            notification.setReadStatus(ReadStatus.READ);
            notificationsDAO.updateNotification(notification);
        }
    }

    /**
     * Retrieve all notifications for a user
     */
    public List<NotificationDTO> getUserNotifications(User user) {
        return notificationsDAO.findNotificationsByUserId(user).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve only unread notifications
     */
    public List<NotificationDTO> getUnreadUserNotifications(User user) {
        return notificationsDAO.findUnreadNotificationsByUser(user).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Notifications for payment holds, captures, transfers, and refunds
     */
    @Transactional
    public Notifications sendHoldNotification(User learner, BigDecimal amount, String paymentIntentId) {
        Notifications notification = new Notifications();
        notification.setUser(learner);
        notification.setNotificationType("PAYMENT_HELD");
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(String.format(
                "A hold of %sJOD has been placed on your card.", amount
        ));
        return notificationsDAO.saveNotification(notification);
    }

    @Transactional
    public Notifications sendTransferNotification(User instructor, BigDecimal amount, String chargeId) {
        Notifications notification = new Notifications();
        notification.setUser(instructor);
        notification.setNotificationType("FUNDS_TRANSFERRED");
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(String.format(
                "$%s has been transferred to your account.", amount
        ));
        return notificationsDAO.saveNotification(notification);
    }

    @Transactional
    public Notifications sendRefundNotification(User learner, BigDecimal amount, String refundId) {
        Notifications notification = new Notifications();
        notification.setUser(learner);
        notification.setNotificationType("REFUND_ISSUED");
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(String.format(
                "%sJOD has been refunded to your card.", amount
        ));
        return notificationsDAO.saveNotification(notification);
    }

    @Transactional
    public Notifications sendCancelNotification(User learner, BigDecimal amount, String paymentIntentId) {
        Notifications notification = new Notifications();
        notification.setUser(learner);
        notification.setNotificationType("PAYMENT_RELEASED");
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(String.format(
                "Your payment hold of %sJOD has been released.", amount
        ));
        return notificationsDAO.saveNotification(notification);
    }

    @Transactional
    public Notifications sendAcceptPostNotification(User learner) {

        Notifications notification = new Notifications();
        notification.setUser(learner);
        notification.setNotificationType("POST_ACCEPTED");
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(String.format(
                "Your post has been accepted."
        ));
        return notificationsDAO.saveNotification(notification);
    }

    @Transactional
    public Notifications sendRejectPostNotification(User learner) {
        Notifications notification = new Notifications();
        notification.setUser(learner);
        notification.setNotificationType("POST_REJECTED");
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(String.format(
                "Your post has been rejected."
        ));

        return notificationsDAO.saveNotification(notification);
    }

    public Notifications findNotificationById(Long notificationId) {
        return notificationsDAO.findNotificationById(notificationId);
    }


    public NotificationDTO deleteNotificationById(Long notificationId) {
        Notifications notification = notificationsDAO.findNotificationById(notificationId);
        if(notification == null) {
            throw new RuntimeException("Notification with id " + notificationId + " not found");
        }
        notificationsDAO.deleteNotification(notificationId);
        return toDTO(notification);
    }

    @Transactional
    public Notifications sendRefundTakenNotification(User instructor,User learner,
                                                     BigDecimal amount,
                                                     String refundId) {
        Notifications notification = new Notifications();
        notification.setUser(instructor);
        notification.setNotificationType("REFUND_TAKEN");
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(String.format(
                "%sJOD has been removed from your account to refund the learner " + learner.getFirstName() + " " + learner.getLastName() + ".",
                amount, refundId
        ));
        return notificationsDAO.saveNotification(notification);
    }



    /**
     * Convert entity to DTO
     */
    private NotificationDTO toDTO(Notifications n) {
        NotificationDTO dto = new NotificationDTO();
        dto.setNotificationId(n.getNotificationId());
        dto.setUserId(n.getUser().getId());
        dto.setNotificationType(n.getNotificationType());
        dto.setReadStatus(n.getReadStatus().name());
        dto.setTimestamp(n.getTimestamp());
        dto.setMessage(n.getMessage());
        dto.setAgreementId(n.getAgreement() != null ? n.getAgreement().getAgreementId() : null);
        return dto;
    }
}

package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.NotificationsDAO;
import com.learnbridge.learn_bridge_back_end.dto.NotificationDTO;
import com.learnbridge.learn_bridge_back_end.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationsDAO notificationsDAO;

    @Transactional
    public Notifications createAgreementRequestNotification(Agreement agreement) {
        Notifications notification = new Notifications();
        notification.setUser(agreement.getLearner().getUser());
        notification.setAgreement(agreement);
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setNotificationType("AGREEMENT_REQUEST");
        notification.setTimestamp(LocalDateTime.now());

        return notificationsDAO.saveNotification(notification);
    }

    @Transactional
    public Notifications createAgreementAcceptedNotification(Agreement agreement) {
        Notifications notification = new Notifications();
        notification.setUser(agreement.getInstructor().getUser());
        notification.setAgreement(agreement);
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setNotificationType("AGREEMENT_ACCEPTED");
        notification.setTimestamp(LocalDateTime.now());

        return notificationsDAO.saveNotification(notification);
    }

    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        Notifications notification = notificationsDAO.findNotificationById(notificationId);
        if (notification != null) {
            notification.setReadStatus(ReadStatus.READ);
            notificationsDAO.updateNotification(notification);
        }
    }

    public List<NotificationDTO> getUserNotifications(User user) {
        List<Notifications> notifications = notificationsDAO.findNotificationsByUserId(user);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getUnreadUserNotifications(User user) {
        List<Notifications> notifications = notificationsDAO.findUnreadNotificationsByUser(user);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public Notifications createAgreementRejectedNotification(Agreement agreement) {
        Notifications notification = new Notifications();
        // send *to* the instructor
        notification.setUser(agreement.getInstructor().getUser());
        notification.setAgreement(agreement);
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setNotificationType("AGREEMENT_REJECTED");
        notification.setTimestamp(LocalDateTime.now());
        return notificationsDAO.saveNotification(notification);
    }

    private NotificationDTO convertToDTO(Notifications notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setNotificationId(notification.getNotificationId());
        dto.setUserId(notification.getUser().getId());
        dto.setAgreementId(notification.getAgreement() != null ? notification.getAgreement().getAgreementId() : null);
        dto.setNotificationType(notification.getNotificationType());
        dto.setReadStatus(notification.getReadStatus().toString());
        dto.setTimestamp(notification.getTimestamp());

        // Include additional info based on notification type
        if (notification.getAgreement() != null) {
            Agreement agr = notification.getAgreement();
            switch (notification.getNotificationType()) {
                case "AGREEMENT_REQUEST":
                    Instructor instr = agr.getInstructor();
                    dto.setMessage("Instructor "
                            + instr.getFirstName() + " " + instr.getLastName()
                            + " requested an agreement for your post: "
                            + agr.getPost().getSubject());
                    break;
                case "AGREEMENT_ACCEPTED":
                    Learner learner = agr.getLearner();
                    dto.setMessage("Learner "
                            + learner.getFirstName() + " " + learner.getLastName()
                            + " accepted your agreement request for post: "
                            + agr.getPost().getSubject());
                    break;
                case "AGREEMENT_REJECTED":
                    Learner l = agr.getLearner();
                    dto.setMessage("Learner "
                            + l.getFirstName() + " " + l.getLastName()
                            + " rejected your agreement request for post: "
                            + agr.getPost().getSubject());
                    break;
            }
        }

        return dto;
    }

    public Notifications findNotificationById(Long notificationId) {
        return notificationsDAO.findNotificationById(notificationId);
    }
}
package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.NotificationsDAO;
import com.learnbridge.learn_bridge_back_end.dto.NotificationDTO;
import com.learnbridge.learn_bridge_back_end.entity.*;
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


    // Instructor asking for agreement notification
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


    // Learner asking for agreement notification
    @Transactional
    public Notifications createLearnerRequestNotification(Agreement agreement) {
        Notifications notification = new Notifications();

        // send *to* the instructor
        notification.setUser(agreement.getInstructor().getUser());
        notification.setAgreement(agreement);
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setNotificationType("LEARNER_REQUEST");
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

    @Transactional
    public Notifications createLearnerRequestAcceptedNotification(Agreement ag) {
        Notifications n = new Notifications();
        // send *to* the learner
        n.setUser(ag.getLearner().getUser());
        n.setAgreement(ag);
        n.setNotificationType("LEARNER_REQUEST_ACCEPTED");
        n.setReadStatus(ReadStatus.UNREAD);
        n.setTimestamp(LocalDateTime.now());
        return notificationsDAO.saveNotification(n);
    }

    @Transactional
    public Notifications createLearnerRequestRejectedNotification(Agreement ag) {
        Notifications n = new Notifications();
        n.setUser(ag.getLearner().getUser());
        n.setAgreement(ag);
        n.setNotificationType("LEARNER_REQUEST_REJECTED");
        n.setReadStatus(ReadStatus.UNREAD);
        n.setTimestamp(LocalDateTime.now());
        return notificationsDAO.saveNotification(n);
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
                case "LEARNER_REQUEST":
                    Learner l2 = agr.getLearner();
                    dto.setMessage("Learner "
                            + l2.getFirstName() + " " + l2.getLastName()
                            + " requested your agreement at price "
                            + agr.getPrice()); // or agr.getPost().getPrice() if you kept a post
                    break;
                case "LEARNER_REQUEST_ACCEPTED":
                    dto.setMessage("Instructor "
                            + agr.getInstructor().getFirstName() + " accepted your request.");
                    break;

                case "LEARNER_REQUEST_REJECTED":
                    dto.setMessage("Instructor "
                            + agr.getInstructor().getFirstName() + " declined your request.");
                    break;
            }
        }

        return dto;
    }



    public void sendHoldNotification(User learner,
                                     BigDecimal amount,
                                     String paymentIntentId) {
        Notifications notification = new Notifications();
        notification.setUser(learner);
        notification.setReadStatus(ReadStatus.UNREAD);
        notification.setNotificationType("PAYMENT_HELD");
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(String.format(
                "We’ve placed a hold of $%s on your card (PaymentIntent %s).",
                amount, paymentIntentId
        ));
        notificationsDAO.saveNotification(notification);
    }

    public void sendTransferNotification(User instructor,
                                         BigDecimal amount,
                                         String chargeId) {
        Notifications n = new Notifications();
        n.setUser(instructor);
        n.setReadStatus(ReadStatus.UNREAD);
        n.setNotificationType("FUNDS_TRANSFERRED");
        n.setTimestamp(LocalDateTime.now());
        n.setMessage(String.format(
                "You’ve received $%s for your session (Charge %s).",
                amount, chargeId
        ));
        notificationsDAO.saveNotification(n);
    }

    public Notifications findNotificationById(Long notificationId) {
        return notificationsDAO.findNotificationById(notificationId);
    }
}
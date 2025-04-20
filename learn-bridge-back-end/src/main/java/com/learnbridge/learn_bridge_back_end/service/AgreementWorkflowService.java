package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dto.AgreementRequestDTO;
import com.learnbridge.learn_bridge_back_end.dto.AgreementResponseDTO;
import com.learnbridge.learn_bridge_back_end.dto.NotificationDTO;
import com.learnbridge.learn_bridge_back_end.dto.SessionDTO;
import com.learnbridge.learn_bridge_back_end.entity.Agreement;
import com.learnbridge.learn_bridge_back_end.entity.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgreementWorkflowService {

    @Autowired
    private AgreementService agreementService;

    @Autowired
    private NotificationService notificationsService;

    @Autowired
    private SessionService sessionService;


      // Instructor requests an agreement for a post

    @Transactional
    public AgreementResponseDTO requestAgreement(Long instructorId, Long learnerId, Long postId) {
        return agreementService.createAgreement(instructorId, learnerId, postId);
    }


      // Learner accepts an agreement offer from a notification

    @Transactional
    public SessionDTO acceptAgreementOffer(Long notificationId) {
        // Mark notification as read
        notificationsService.markNotificationAsRead(notificationId);

        // Get the agreement from the notification
        Notifications notification = notificationsService.findNotificationById(notificationId);
        if (notification == null) {
            throw new RuntimeException("Notification not found with id: " + notificationId);
        }

        Agreement agreement = notification.getAgreement();
        if (agreement == null) {
            throw new RuntimeException("No agreement found for notification: " + notificationId);
        }

        // Create a notification for the instructor that the agreement was accepted
        notificationsService.createAgreementAcceptedNotification(agreement);

        // Create a session from the agreement
        return sessionService.createSessionFromAgreement(agreement.getAgreementId());
    }
}
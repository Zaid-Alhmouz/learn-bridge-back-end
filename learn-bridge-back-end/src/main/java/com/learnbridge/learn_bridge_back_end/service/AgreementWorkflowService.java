package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.PostDAO;
import com.learnbridge.learn_bridge_back_end.dto.AgreementRequestDTO;
import com.learnbridge.learn_bridge_back_end.dto.AgreementResponseDTO;
import com.learnbridge.learn_bridge_back_end.dto.NotificationDTO;
import com.learnbridge.learn_bridge_back_end.dto.SessionDTO;
import com.learnbridge.learn_bridge_back_end.entity.Agreement;
import com.learnbridge.learn_bridge_back_end.entity.Notifications;
import com.learnbridge.learn_bridge_back_end.entity.Post;
import com.learnbridge.learn_bridge_back_end.entity.PostStatus;
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

    @Autowired
    private PostDAO postDAO;


      // Instructor requests an agreement for a post

    @Transactional
    public AgreementResponseDTO requestAgreement(Long instructorId, Long learnerId, Long postId) {
        return agreementService.createAgreement(instructorId, learnerId, postId);
    }


      // Learner accepts an agreement offer from a notification

    @Transactional
    public SessionDTO acceptAgreementOffer(Long notificationId) {
        // mark notification as read
        notificationsService.markNotificationAsRead(notificationId);

        // get the agreement from the notification
        Notifications notification = notificationsService.findNotificationById(notificationId);
        if (notification == null) {
            throw new RuntimeException("Notification not found with id: " + notificationId);
        }

        Agreement agreement = notification.getAgreement();
        if (agreement == null) {
            throw new RuntimeException("No agreement found for notification: " + notificationId);
        }

        // create a notification for the instructor that the agreement was accepted
        notificationsService.createAgreementAcceptedNotification(agreement);

        // change post status to ON_HOLD
        Post post = agreement.getPost();
        post.setPostStatus(PostStatus.ON_HOLD);
        postDAO.updatePost(post);

        // Create a session from the agreement
        return sessionService.createSessionFromAgreement(agreement.getAgreementId());
    }

    @Transactional
    public void rejectAgreementOffer(Long notificationId) {

        notificationsService.markNotificationAsRead(notificationId);


        Notifications notice = notificationsService.findNotificationById(notificationId);
        if (notice == null || notice.getAgreement() == null) {
            throw new RuntimeException("Notification or agreement not found");
        }
        Agreement agreement = notice.getAgreement();

        notificationsService.createAgreementRejectedNotification(agreement);

    }
}
package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.InstructorDAO;
import com.learnbridge.learn_bridge_back_end.dao.LearnerDAO;
import com.learnbridge.learn_bridge_back_end.dao.NotificationsDAO;
import com.learnbridge.learn_bridge_back_end.dao.PostDAO;
import com.learnbridge.learn_bridge_back_end.dto.AgreementRequestDTO;
import com.learnbridge.learn_bridge_back_end.dto.AgreementResponseDTO;
import com.learnbridge.learn_bridge_back_end.dto.NotificationDTO;
import com.learnbridge.learn_bridge_back_end.dto.SessionDTO;
import com.learnbridge.learn_bridge_back_end.entity.*;
import com.stripe.exception.StripeException;
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

    @Autowired
    NotificationsDAO notificationsDAO;



      // Instructor requests an agreement from a Learner via post
    @Transactional
    public AgreementResponseDTO requestAgreement(Long instructorId, Long learnerId, Long postId) {
        return agreementService.createAgreement(instructorId, learnerId, postId);
    }


    // Learner requests agreement from Instructor via instructor's profile
    @Transactional
    public AgreementResponseDTO learnerRequestsInstructor(Long learnerId, Long instructorId) {
        return agreementService.createLearnerInitiatedAgreement(learnerId, instructorId);
    }

    // Learner accepts an agreement offer from a notification
    @Transactional
    public SessionDTO acceptInstructorAgreementOffer(Long notificationId) throws StripeException {
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

        notificationsDAO.deleteNotification(notificationId);
        // create a notification for the instructor that the agreement was accepted
        notificationsService.createAgreementAcceptedNotification(agreement);

        // change post status to ON_HOLD
        Post post = agreement.getPost();
        post.setPostStatus(PostStatus.ON_HOLD);
        postDAO.updatePost(post);

        // create a session from the agreement
        return sessionService.createSessionFromInstructorAgreement(agreement.getAgreementId());
    }




    @Transactional
    public void rejectAgreementOffer(Long notificationId) {

        notificationsService.markNotificationAsRead(notificationId);


        Notifications notice = notificationsDAO.findNotificationById(notificationId);
        if (notice == null || notice.getAgreement() == null) {
            throw new RuntimeException("Notification or agreement not found");
        }
        Agreement agreement = notice.getAgreement();

        notificationsDAO.deleteNotification(notificationId);
        notificationsService.createAgreementRejectedNotification(agreement);

    }

    /** Instructor accepts a learner-initiated request */
    @Transactional
    public SessionDTO acceptLearnerRequest(Long notificationId) throws StripeException {
        // mark the instructor’s notification as read
        notificationsService.markNotificationAsRead(notificationId);

        // load the notification and agreement
        Notifications note = notificationsDAO.findNotificationById(notificationId);
        if (note == null || note.getAgreement() == null) {
            throw new RuntimeException("Notification or agreement not found: " + notificationId);
        }
        Agreement ag = note.getAgreement();

        // notify the learner that you’ve accepted their request
        notificationsService.createLearnerRequestAcceptedNotification(ag);

        notificationsDAO.deleteNotification(notificationId);
        // go straight to session creation
        return sessionService.createSessionFromLearnerAgreement(ag.getAgreementId());
    }


    @Transactional
    public void rejectLearnerRequest(Long notificationId) {
        notificationsService.markNotificationAsRead(notificationId);

        Notifications note = notificationsService.findNotificationById(notificationId);
        if (note == null || note.getAgreement() == null) {
            throw new RuntimeException("Notification or agreement not found: " + notificationId);
        }
        Agreement ag = note.getAgreement();

        notificationsDAO.deleteNotification(notificationId);

        // notify the learner that you’ve declined
        notificationsService.createLearnerRequestRejectedNotification(ag);
    }

}
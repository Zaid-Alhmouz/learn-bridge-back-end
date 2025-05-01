package com.learnbridge.learn_bridge_back_end.controller;

import com.learnbridge.learn_bridge_back_end.dto.AgreementRequestDTO;
import com.learnbridge.learn_bridge_back_end.dto.AgreementResponseDTO;
import com.learnbridge.learn_bridge_back_end.dto.NotificationDTO;
import com.learnbridge.learn_bridge_back_end.dto.SessionDTO;
import com.learnbridge.learn_bridge_back_end.entity.User;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.AgreementWorkflowService;
import com.learnbridge.learn_bridge_back_end.service.NotificationService;
import com.learnbridge.learn_bridge_back_end.service.NotificationService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agreements")
@CrossOrigin(origins = "http://localhost:4200")
public class AgreementController {

    @Autowired
    private AgreementWorkflowService agreementWorkflowService;

    @Autowired
    private NotificationService notificationsService;


    @PostMapping("/request/{learnerId}/{postId}")
    public ResponseEntity<AgreementResponseDTO> requestAgreement(
            @AuthenticationPrincipal SecurityUser loggedUser, @PathVariable Long learnerId,
            @PathVariable Long postId) {

        Long instructorId = loggedUser.getUser().getId();
        AgreementResponseDTO response = agreementWorkflowService.requestAgreement(instructorId, learnerId, postId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/request-instructor/{instructorId}")
    public ResponseEntity<AgreementResponseDTO> requestInstructor(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable Long instructorId) {
        Long learnerId = user.getUser().getId();
        AgreementResponseDTO dto =
                agreementWorkflowService.learnerRequestsInstructor(learnerId, instructorId);
        return ResponseEntity.ok(dto);
    }



    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@AuthenticationPrincipal SecurityUser securityUser) {
        User user = securityUser.getUser();
        List<NotificationDTO> notifications = notificationsService.getUserNotifications(user);
        return ResponseEntity.ok(notifications);
    }


    @PostMapping("/notifications/{notificationId}/accept")
    public ResponseEntity<SessionDTO> acceptInstructorAgreementOffer(@PathVariable Long notificationId) throws StripeException {
        SessionDTO session = agreementWorkflowService.acceptInstructorAgreementOffer(notificationId);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/notifications/{notificationId}/reject")
    public ResponseEntity<Void> rejectAgreementOffer(@PathVariable Long notificationId) {
        agreementWorkflowService.rejectAgreementOffer(notificationId);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/notifications/{notificationId}/accept-learner-request")
    public ResponseEntity<SessionDTO> acceptLearnerRequest(
            @PathVariable Long notificationId) throws StripeException {
        SessionDTO session = agreementWorkflowService.acceptLearnerRequest(notificationId);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/notifications/{notificationId}/reject-learner-request")
    public ResponseEntity<Void> rejectLearnerRequest(
            @PathVariable Long notificationId) {
        agreementWorkflowService.rejectLearnerRequest(notificationId);
        return ResponseEntity.ok().build();
    }
}
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


    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@AuthenticationPrincipal SecurityUser securityUser) {
        User user = securityUser.getUser();
        List<NotificationDTO> notifications = notificationsService.getUserNotifications(user);
        return ResponseEntity.ok(notifications);
    }


    @PostMapping("/notifications/{notificationId}/accept")
    public ResponseEntity<SessionDTO> acceptAgreementOffer(@PathVariable Long notificationId) {
        SessionDTO session = agreementWorkflowService.acceptAgreementOffer(notificationId);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/notifications/{notificationId}/reject")
    public ResponseEntity<Void> rejectAgreementOffer(@PathVariable Long notificationId) {
        agreementWorkflowService.rejectAgreementOffer(notificationId);
        return ResponseEntity.ok().build();
    }
}
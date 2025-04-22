package com.learnbridge.learn_bridge_back_end.controller;

import com.learnbridge.learn_bridge_back_end.dao.UserDAO;
import com.learnbridge.learn_bridge_back_end.dto.SessionDTO;
import com.learnbridge.learn_bridge_back_end.entity.*;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.SessionService;
import com.learnbridge.learn_bridge_back_end.util.SessionMapper;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/session")
@CrossOrigin(origins = "http://localhost:4200")
public class SessionController {

    @Autowired
    SessionService sessionService;

    @Autowired
    UserDAO userDAO;

    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionDTO> getSessionById(@PathVariable Long sessionId) {
        SessionDTO session = sessionService.getSessionById(sessionId);
        return ResponseEntity.ok(session);
    }


    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<SessionDTO>> getSessionsByInstructor(@PathVariable Long instructorId) {
        List<SessionDTO> sessions = sessionService.getSessionsByInstructor(instructorId);
        if (sessions == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(sessions);
    }


    @GetMapping("/agreement/{agreementId}")
    public ResponseEntity<List<SessionDTO>> getSessionsByAgreement(@PathVariable Long agreementId) {
        List<SessionDTO> sessions = sessionService.getSessionsByAgreement(agreementId);
        return ResponseEntity.ok(sessions);
    }



    @PutMapping("/{sessionId}/status")
    public ResponseEntity<SessionDTO> updateSessionStatus(
            @PathVariable Long sessionId,
            @RequestParam SessionStatus status) {
        SessionDTO updatedSession = sessionService.updateSessionStatus(sessionId, status);
        return ResponseEntity.ok(updatedSession);
    }


    // change session status from 'ONGOING' to 'FINISHED'
    @PutMapping("/cancel-session/{sessionId}")
    public ResponseEntity<SessionDTO> cancelSession(@AuthenticationPrincipal SecurityUser loggedUser, @PathVariable Long sessionId) {

        Long cancelledById = loggedUser.getUser().getId();
        SessionDTO cancelledSession = sessionService.cancelSession(sessionId, cancelledById);

        if (cancelledSession == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(cancelledSession);
    }


    // change the sessions status from ongoing to 'FINISHED'
    @PutMapping("/finish-session/{sessionId}")
    public ResponseEntity<SessionDTO> finishSession(@PathVariable Long sessionId, @AuthenticationPrincipal SecurityUser loggedUser) {

        Long cancelledById = loggedUser.getUser().getId();
        SessionDTO cancelledSession = sessionService.finishSession(sessionId, cancelledById);
        if (cancelledSession == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(cancelledSession);
    }


    // retrieve all sessions for user based on his role
    @GetMapping("/session-history")
    public ResponseEntity<List<SessionDTO>> getMySessions(@AuthenticationPrincipal SecurityUser securityUser) {
        User user = securityUser.getUser();
        Long userId = user.getId();

        List<Session> sessionsHistory = sessionService.getUserSessions(user);

        if (sessionsHistory == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(SessionMapper.toSessionDTOList(sessionsHistory));
    }


    // retrieve ongoing sessions for user based on role
    @GetMapping("/my-sessions/ongoing")
    public ResponseEntity<List<SessionDTO>> getMyOngoingSessions(@AuthenticationPrincipal SecurityUser securityUser) {
        User user = securityUser.getUser();
        Long userId = user.getId();

        List<Session> sessionsHistory = sessionService.getActiveUserSessions(user);

        if (sessionsHistory == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(SessionMapper.toSessionDTOList(sessionsHistory));
    }


}
package com.learnbridge.learn_bridge_back_end.controller;

import com.learnbridge.learn_bridge_back_end.dto.SessionDTO;
import com.learnbridge.learn_bridge_back_end.entity.SessionStatus;
import com.learnbridge.learn_bridge_back_end.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/session")
@CrossOrigin(origins = "http://localhost:4200")
public class SessionController {

    @Autowired
    SessionService sessionService;

    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionDTO> getSessionById(@PathVariable Long sessionId) {
        SessionDTO session = sessionService.getSessionById(sessionId);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<SessionDTO>> getSessionsByInstructor(@PathVariable Long instructorId) {
        List<SessionDTO> sessions = sessionService.getSessionsByInstructor(instructorId);
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
}
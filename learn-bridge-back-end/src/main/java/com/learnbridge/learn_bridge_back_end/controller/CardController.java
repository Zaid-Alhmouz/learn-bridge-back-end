package com.learnbridge.learn_bridge_back_end.controller;

import com.learnbridge.learn_bridge_back_end.dto.AddCardRequest;
import com.learnbridge.learn_bridge_back_end.dto.AddCardResponse;
import com.learnbridge.learn_bridge_back_end.entity.Card;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.CardService;
import com.learnbridge.learn_bridge_back_end.util.CardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "http://localhost:4200")
public class CardController {

    @Autowired
    private CardService cardService;
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'LEARNER')")
    @PostMapping("/add")
    public ResponseEntity<?> addCard(@RequestBody AddCardRequest cardRequest, @AuthenticationPrincipal SecurityUser loggedUser) {
        try {
            AddCardResponse savedCard = cardService.addCard(cardRequest, loggedUser.getUser().getId());
            return ResponseEntity.ok(savedCard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/set-default/{cardId}")
    public ResponseEntity<?> setDefaultCard(@PathVariable Long cardId, @AuthenticationPrincipal SecurityUser loggedUser) {
        Long userId = loggedUser.getUser().getId();

        AddCardRequest editedCard = cardService.setDefaultCard(cardId, userId);

        if (editedCard == null) {
           return ResponseEntity.badRequest().body(editedCard);
        }
        return ResponseEntity.ok(editedCard);
    }
}

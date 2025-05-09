package com.learnbridge.learn_bridge_back_end.controller;

import com.learnbridge.learn_bridge_back_end.dto.AddCardRequest;
import com.learnbridge.learn_bridge_back_end.dto.AddCardResponse;
import com.learnbridge.learn_bridge_back_end.entity.Card;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.CardService;
import com.learnbridge.learn_bridge_back_end.util.CardMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "http://localhost:4200")
public class CardController {

    @Autowired
    private CardService cardService;

    @PostMapping("/add")
    public ResponseEntity<?> addCard(
            @Valid @RequestBody AddCardRequest cardRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal SecurityUser loggedUser) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        try {
            AddCardResponse savedCard = cardService.addCard(cardRequest, loggedUser.getUser().getId());
            return ResponseEntity.ok(savedCard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/set-default/{cardId}")
    public ResponseEntity<?> setDefaultCard(
            @PathVariable Long cardId,
            @AuthenticationPrincipal SecurityUser loggedUser) {
        Long userId = loggedUser.getUser().getId();

        AddCardRequest editedCard = cardService.setDefaultCard(cardId, userId);

        if (editedCard == null) {
            return ResponseEntity.badRequest().body("Unable to set default card.");
        }
        return ResponseEntity.ok(editedCard);
    }
}

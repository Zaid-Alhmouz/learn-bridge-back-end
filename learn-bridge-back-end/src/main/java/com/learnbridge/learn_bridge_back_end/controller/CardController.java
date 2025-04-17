package com.learnbridge.learn_bridge_back_end.controller;

import com.learnbridge.learn_bridge_back_end.dto.AddCardRequest;
import com.learnbridge.learn_bridge_back_end.entity.Card;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.CardService;
import com.learnbridge.learn_bridge_back_end.util.CardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "http://localhost:4200")
public class CardController {

    @Autowired
    private CardService cardService;

    @PostMapping("/add")
    public ResponseEntity<?> addCard(@RequestBody AddCardRequest cardRequest, @AuthenticationPrincipal SecurityUser loggedUser) {
        try {
            Card savedCard = cardService.addCard(cardRequest, loggedUser);
            return ResponseEntity.ok(CardMapper.toAddCardRequest(savedCard));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

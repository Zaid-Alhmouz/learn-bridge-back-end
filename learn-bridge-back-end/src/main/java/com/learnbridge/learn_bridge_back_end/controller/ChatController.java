package com.learnbridge.learn_bridge_back_end.controller;

import com.learnbridge.learn_bridge_back_end.dto.AdminChatReviewDTO;
import com.learnbridge.learn_bridge_back_end.dto.ChatSummaryDTO;
import com.learnbridge.learn_bridge_back_end.dto.MessageDTO;
import com.learnbridge.learn_bridge_back_end.entity.User;
import com.learnbridge.learn_bridge_back_end.entity.UserRole;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {

    @Autowired
    private ChatService chatService;


    @PostMapping("/send-message/{chatId}")
    public ResponseEntity<MessageDTO> sendMessage(@PathVariable Long chatId, @RequestBody MessageDTO messageDTO, @AuthenticationPrincipal SecurityUser loggedUser) {

        Long senderId = loggedUser.getUser().getId();
        String messageContent = messageDTO.getContent();

        if(senderId == null || chatId == null || messageContent == null || messageContent.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        MessageDTO sentMessage = chatService.sendMessage(messageContent, senderId, chatId);

        if (sentMessage == null)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        else
        {
            return new ResponseEntity<>(sentMessage, HttpStatus.OK);
        }
    }

    @DeleteMapping("/delete-message/{messageId}")
    public ResponseEntity<MessageDTO> deleteMessage(@PathVariable Long messageId, @AuthenticationPrincipal SecurityUser loggedUser) {

        Long userId = loggedUser.getUser().getId();

        MessageDTO messageToBeDeleted = chatService.deleteMessage(messageId, userId);

        if (messageToBeDeleted == null)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(messageToBeDeleted, HttpStatus.OK);
    }

    @GetMapping("/all-messages/{chatId}")
    public ResponseEntity<List<MessageDTO>> getAllMessages(@PathVariable Long chatId) {

        List<MessageDTO> messages = chatService.getAllMessages(chatId);
        if (messages == null)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping("/my-chats")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'LEARNER')")
    public ResponseEntity<List<ChatSummaryDTO>> getMyChats(
            @AuthenticationPrincipal SecurityUser loggedUser) {
        try {
            Long userId = loggedUser.getUser().getId();
            UserRole role = loggedUser.getUser().getUserRole();

            List<ChatSummaryDTO> chats = chatService.getAllChatsForUser(userId, role);
            return ResponseEntity.ok(chats != null ? chats : Collections.emptyList());
        } catch (Exception e) {
            // on any unexpected error, return empty list rather than 500
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/admin/review-chat/{chatId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminChatReviewDTO> reviewChat(@PathVariable Long chatId) {
        AdminChatReviewDTO dto = chatService.reviewChat(chatId);
        return ResponseEntity.ok(dto);
    }

}

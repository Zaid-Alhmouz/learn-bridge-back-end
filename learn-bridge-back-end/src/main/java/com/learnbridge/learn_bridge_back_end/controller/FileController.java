package com.learnbridge.learn_bridge_back_end.controller;

import com.learnbridge.learn_bridge_back_end.dao.ChatDAO;
import com.learnbridge.learn_bridge_back_end.dto.FileDTO;
import com.learnbridge.learn_bridge_back_end.entity.Chat;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.List;

@RestController
@RequestMapping("/api/file")
@CrossOrigin(origins = "http://localhost:4200")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private ChatDAO chatDAO;

    @PostMapping(value = "/upload/{chatId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDTO> uploadFile(
            @PathVariable Long chatId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal SecurityUser loggedUser
    )   {
            Long senderId = loggedUser.getUser().getId();
            FileDTO fileDTO = fileService.uploadFile(senderId, chatId, file);

            if (fileDTO == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(fileDTO, HttpStatus.OK);
        }

    @GetMapping("/chat-files/{chatId}")
    public ResponseEntity<List<FileDTO>> getChatFiles(@PathVariable Long chatId) {

        Chat chat = chatDAO.findChatById(chatId);
        if (chat == null) {
            return ResponseEntity.notFound().build();
        }

        List<FileDTO> chatFiles = fileService.getAllFilesForChat(chatId);
            return new ResponseEntity<>(chatFiles, HttpStatus.OK);

    }
}

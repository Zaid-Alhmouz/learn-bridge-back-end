package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.ChatDAO;
import com.learnbridge.learn_bridge_back_end.dao.FileDAO;
import com.learnbridge.learn_bridge_back_end.dao.UserDAO;
import com.learnbridge.learn_bridge_back_end.dto.FileDTO;
import com.learnbridge.learn_bridge_back_end.entity.Chat;
import com.learnbridge.learn_bridge_back_end.entity.File;
import com.learnbridge.learn_bridge_back_end.entity.User;
import com.learnbridge.learn_bridge_back_end.util.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileService {

    @Autowired
    private FileDAO fileDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ChatDAO chatDAO;


    @Transactional
    public FileDTO uploadFile(Long senderId, Long chatId, MultipartFile file) {
        User sender = userDAO.findUserById(senderId);
        Chat chat = chatDAO.findChatById(chatId);

        if(sender == null || chat == null)
        {
            throw new IllegalArgumentException("Sender or chat is null");
        }

        try {

            File uploadedFile = new File();

            uploadedFile.setSender(sender);
            uploadedFile.setChat(chat);
            uploadedFile.setFileName(file.getOriginalFilename());
            uploadedFile.setFileType(file.getContentType());
            uploadedFile.setFileSize(file.getSize());
            uploadedFile.setFileData(file.getBytes());
            fileDAO.saveFile(uploadedFile);
            return FileMapper.toFileDTO(uploadedFile);
        }
        catch (IOException e)
        {
            throw new RuntimeException("upload file error");
        }
    }
}

package com.learnbridge.learn_bridge_back_end.service;


import com.learnbridge.learn_bridge_back_end.dao.ChatDAO;
import com.learnbridge.learn_bridge_back_end.dao.TextMessageDAO;
import com.learnbridge.learn_bridge_back_end.dao.UserDAO;
import com.learnbridge.learn_bridge_back_end.dto.MessageDTO;
import com.learnbridge.learn_bridge_back_end.entity.Chat;
import com.learnbridge.learn_bridge_back_end.entity.TextMessage;
import com.learnbridge.learn_bridge_back_end.entity.User;
import com.learnbridge.learn_bridge_back_end.util.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    ChatDAO chatDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    TextMessageDAO textMessageDAO;




    // create message and store it in database
    public MessageDTO sendMessage(String message, Long senderId, Long chatId) {

        User sender = userDAO.findUserById(senderId);
        Chat chat = chatDAO.findChatById(chatId);

        TextMessage textMessage = new TextMessage();
        textMessage.setSender(sender);
        textMessage.setChat(chat);
        textMessage.setContent(message);
        textMessage.setSentAt(LocalDateTime.now());
        textMessage.setSenderName(sender.getFirstName() + " " + sender.getLastName());

        textMessageDAO.saveTextMessage(textMessage);

        return MessageMapper.toMessageDTO(textMessage);
    }

    // delete message service by message id
    public MessageDTO deleteMessage(Long messageId, Long userId) {

        TextMessage messageToBeDeleted = textMessageDAO.findTextMessageById(messageId);

        if(messageToBeDeleted == null)
        {
            throw new IllegalArgumentException("messageId does not exist");
        }

        if(userId != messageToBeDeleted.getSender().getId())
        {
            throw new IllegalArgumentException("the user does not own this message to delete it");
        }

        textMessageDAO.deleteTextMessageById(messageId);

        return MessageMapper.toMessageDTO(messageToBeDeleted);
    }

    public List<MessageDTO> getAllMessages(Long chatId) {
        Chat chat = chatDAO.findChatById(chatId);
        if (chat == null)
        {
            throw new IllegalArgumentException("chatId does not exist");
        }
        List<TextMessage> allMessages = textMessageDAO.findTextMessageByChatId(chatId);

        if (allMessages == null)
        {
            throw new IllegalArgumentException("Messages does not exist in this chat" + chatId);
        }
        return MessageMapper.toMessageDTOList(allMessages);
    }
}

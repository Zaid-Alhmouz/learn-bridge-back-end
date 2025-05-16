package com.learnbridge.learn_bridge_back_end.service;


import com.learnbridge.learn_bridge_back_end.dao.*;
import com.learnbridge.learn_bridge_back_end.dto.AdminChatReviewDTO;
import com.learnbridge.learn_bridge_back_end.dto.ChatSummaryDTO;
import com.learnbridge.learn_bridge_back_end.dto.FileDTO;
import com.learnbridge.learn_bridge_back_end.dto.MessageDTO;
import com.learnbridge.learn_bridge_back_end.entity.*;
import com.learnbridge.learn_bridge_back_end.util.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    ChatDAO chatDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    TextMessageDAO textMessageDAO;

    @Autowired private SessionDAO sessionDAO;

    @Autowired private SessionParticipantsDAO participantsDAO;

    @Autowired
    private FileDAO fileDAO;


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

    @Transactional(readOnly = true)
    public List<ChatSummaryDTO> getAllChatsForUser(Long userId, UserRole role) {
        // 1) fetch sessions for this user & role
        List<Session> sessions;
        if (role == UserRole.INSTRUCTOR) {
            sessions = sessionDAO.findSessionByInstructorId(userId);
        } else if (role == UserRole.LEARNER) {
            sessions = sessionDAO.findSessionsByParticipantId(userId);
        } else {
            return Collections.emptyList();
        }

        List<ChatSummaryDTO> result = new ArrayList<>();

        for (Session session : sessions) {
            if (session == null) continue;
            Set<Chat> chats = session.getChats();
            if (chats == null) continue;

            // find the “other” participant name
            String otherName = "";
            if (role == UserRole.LEARNER) {
                // caller is learner → get instructor
                User instr = session.getInstructor();
                if (instr != null) {
                    otherName = instr.getFirstName() + " " + instr.getLastName();
                }
            } else {
                // caller is instructor → find the single learner participant
                SessionParticipants sp = session.getParticipants()
                        .stream()
                        .filter(p -> !p.getLearnerId().equals(userId))
                        .findFirst()
                        .orElse(null);
                if (sp != null && sp.getLearner() != null) {
                    Learner learner = sp.getLearner();
                    otherName = learner.getFirstName() + " " + learner.getLastName();
                }
            }

            // for each chat under this session, emit one DTO
            for (Chat chat : chats) {
                if (chat == null) continue;
                result.add(new ChatSummaryDTO(
                        chat.getChatId(),
                        session.getSessionId(),
                        otherName,
                        session.getSessionStatus()
                ));
            }
        }

        return result;
    }

    /**
     * Retrieves full chat details for admin review.
     */
    @Transactional(readOnly = true)
    public AdminChatReviewDTO reviewChat(Long chatId) {
        Chat chat = chatDAO.findChatById(chatId);
        if (chat == null) {
            throw new IllegalArgumentException("Chat ID not found: " + chatId);
        }

        Session session = chat.getSession();
        Long sessionId = session.getSessionId();
        String sessionStatus = session.getSessionStatus().name();

        // Instructor
        Long instructorId = session.getInstructor().getId();
        String instructorName = session.getInstructor().getFirstName() + " " + session.getInstructor().getLastName();

        // Learner (assumes single participant)
        SessionParticipants participant = session.getParticipants()
                .stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No learner participant found for session " + sessionId));
        Long learnerId = participant.getLearnerId();
        String learnerName = participant.getLearner().getFirstName() + " " + participant.getLearner().getLastName();

        // Messages and files
        List<MessageDTO> messages = textMessageDAO.findTextMessageByChatId(chatId)
                .stream().map(MessageDTO::new)
                .collect(Collectors.toList());
        List<FileDTO> files = fileDAO.findFilesByChatId(chatId)
                .stream().map(FileDTO::new)
                .collect(Collectors.toList());

        return new AdminChatReviewDTO(
                chatId,
                sessionId,
                sessionStatus,
                instructorId,
                instructorName,
                learnerId,
                learnerName,
                messages,
                files
        );
    }

}

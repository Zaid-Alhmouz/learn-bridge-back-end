package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Chat;
import com.learnbridge.learn_bridge_back_end.entity.Session;

import java.util.List;

public interface ChatDAO {

    Chat saveChat(Chat chat);
    Chat updateChat(Chat chat);
    void deleteChatById(Long chatId);
    Chat findChatById(Long chatId);
    List<Chat> findAllChats();
    List<Chat> findChatBySession(Session session);
}

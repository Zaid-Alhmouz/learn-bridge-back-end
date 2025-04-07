package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Chat;

import java.util.List;

public interface ChatDAO {

    void saveChat(Chat chat);
    void updateChat(Chat chat);
    void deleteChatById(Long chatId);
    Chat findChatById(Long chatId);
    List<Chat> findAllChats();
}

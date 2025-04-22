package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.TextMessage;

import java.util.List;

public interface TextMessageDAO {

    TextMessage saveTextMessage(TextMessage textMessage);
    TextMessage updateTextMessage(TextMessage textMessage);
    void deleteTextMessageById(Long textMessageId);
    List<TextMessage> findTextMessageByChatId(Long ChatId);
    List<TextMessage> findAllTextMessages();
    void deleteAllTextMessagesByChatId(Long ChatId);
    TextMessage findTextMessageById(Long textMessageId);
}

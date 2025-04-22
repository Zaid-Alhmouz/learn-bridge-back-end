package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.TextMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class TextMessageDAOImpl implements TextMessageDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public TextMessage saveTextMessage(TextMessage textMessage) {

        entityManager.persist(textMessage);

        return textMessage;
    }

    @Override
    @Transactional
    public TextMessage updateTextMessage(TextMessage textMessage) {
        return entityManager.merge(textMessage);
    }

    @Override
    @Transactional
    public void deleteTextMessageById(Long textMessageId) {
        TextMessage textMessage = entityManager.find(TextMessage.class, textMessageId);
        entityManager.remove(entityManager.contains(textMessage) ? textMessage : entityManager.merge(textMessage));
    }

    @Override
    public List<TextMessage> findTextMessageByChatId(Long ChatId) {
       String sqlStatement = "select t from TextMessage t where t.chat.chatId = :ChatId";
       TypedQuery<TextMessage> query = entityManager.createQuery(sqlStatement, TextMessage.class);
       query.setParameter("ChatId", ChatId);
       return query.getResultList();
    }

    @Override
    public List<TextMessage> findAllTextMessages() {
        return entityManager.createQuery("select t from TextMessage t", TextMessage.class).getResultList();
    }

    @Override
    @Transactional
    public void deleteAllTextMessagesByChatId(Long ChatId) {

        List<TextMessage> textMessages = findTextMessageByChatId(ChatId);
        for (TextMessage textMessage : textMessages) {
            entityManager.remove(entityManager.contains(textMessage) ? textMessage : entityManager.merge(textMessage));
        }
    }

    @Override
    public TextMessage findTextMessageById(Long textMessageId) {

        return entityManager.find(TextMessage.class, textMessageId);
    }
}

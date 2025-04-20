package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Chat;
import com.learnbridge.learn_bridge_back_end.entity.Session;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ChatDAOImpl implements ChatDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Chat saveChat(Chat chat) {

        entityManager.persist(chat);
        return chat;
    }

    @Override
    @Transactional
    public Chat updateChat(Chat chat) {

        return entityManager.merge(chat);
    }

    @Override
    @Transactional
    public void deleteChatById(Long chatId) {

        Chat chat = entityManager.find(Chat.class, chatId);
        entityManager.remove(entityManager.contains(chat) ? chat : entityManager.merge(chat));
    }

    @Override
    public Chat findChatById(Long chatId) {
        return entityManager.find(Chat.class, chatId);
    }

    @Override
    public List<Chat> findAllChats() {
        return entityManager.createQuery("select c from Chat c", Chat.class).getResultList();
    }

    @Override
    public List<Chat> findChatBySession(Session session) {
        TypedQuery<Chat> query = entityManager.createQuery(
                "SELECT c FROM Chat c WHERE c.session = :session",
                Chat.class);
        query.setParameter("session", session);
        return query.getResultList();
    }
}

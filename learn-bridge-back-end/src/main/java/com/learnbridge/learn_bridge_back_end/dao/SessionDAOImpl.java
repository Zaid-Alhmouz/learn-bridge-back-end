package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Session;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class SessionDAOImpl implements SessionDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void saveSession(Session session) {

        entityManager.persist(session);
    }

    @Override
    @Transactional
    public void updateSession(Session session) {

        entityManager.merge(session);
    }

    @Override
    public Session findSessionById(Long sessionId) {
        return entityManager.find(Session.class, sessionId);
    }

    @Override
    @Transactional
    public void deleteSessionById(Long sessionId) {

        Session session = findSessionById(sessionId);
        entityManager.remove(session);
    }

    @Override
    public List<Session> findAllSessions() {

        return entityManager.createQuery("from Session", Session.class).getResultList();
    }
}

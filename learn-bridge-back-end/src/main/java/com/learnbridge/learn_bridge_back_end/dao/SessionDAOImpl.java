package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Agreement;
import com.learnbridge.learn_bridge_back_end.entity.Instructor;
import com.learnbridge.learn_bridge_back_end.entity.Session;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class SessionDAOImpl implements SessionDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Session saveSession(Session session) {

        entityManager.persist(session);
        return session;
    }

    @Override
    @Transactional
    public Session updateSession(Session session) {
       return entityManager.merge(session);
    }

    @Override
    public Session findSessionById(Long sessionId) {
        return entityManager.find(Session.class, sessionId);
    }

    @Override
    @Transactional
    public void deleteSessionById(Long sessionId) {

        Session session = findSessionById(sessionId);
        if (session != null) {
            entityManager.remove(session);
        }
    }

    @Override
    public List<Session> findAllSessions() {

        return entityManager.createQuery("from Session", Session.class).getResultList();
    }

    @Override
    public List<Session> findSessionByInstructorId(Long instructorId) {
        TypedQuery<Session> query = entityManager.createQuery(
                "SELECT s FROM Session s WHERE s.instructor.userId = :instructor_id",
                Session.class);
        query.setParameter("instructor_id", instructorId);
        return query.getResultList();
    }

    @Override
    public List<Session> findByAgreementId(Long agreementId) {
        TypedQuery<Session> query = entityManager.createQuery(
                "SELECT s FROM Session s WHERE s.agreement.agreementId = :agreement_id",
                Session.class);
        query.setParameter("agreement_id", agreementId);
        return query.getResultList();
    }
}

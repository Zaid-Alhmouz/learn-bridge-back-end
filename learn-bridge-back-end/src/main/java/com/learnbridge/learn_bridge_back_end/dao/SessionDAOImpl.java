package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Session;
import com.learnbridge.learn_bridge_back_end.entity.SessionStatus;
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
    public List<Session> findOngoingSessionByInstructorId(Long sessionId) {
        TypedQuery<Session> query = entityManager.createQuery("SELECT s from Session s WHERE s.instructor.userId = :instructor_id AND s.sessionStatus = :sessionStatus", Session.class);
        query.setParameter("instructor_id", sessionId);
        query.setParameter("sessionStatus", SessionStatus.ONGOING);

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

    @Override
    public List<Session> findSessionsByParticipantId(Long participantId) {
        TypedQuery<Session> query = entityManager.createQuery(
                "SELECT s FROM Session s JOIN s.participants p WHERE p.learnerId = :participantId",
                Session.class);
        query.setParameter("participantId", participantId);
        return query.getResultList();
    }

    @Override
    public List<Session> findOngoingSessionsByParticipantId(Long participantId) {
        TypedQuery<Session> query = entityManager.createQuery(
                "SELECT s FROM Session s JOIN s.participants p WHERE p.learnerId = :participantId AND s.sessionStatus = :sessionStatus",
                Session.class);
        query.setParameter("participantId", participantId);
        query.setParameter("sessionStatus", SessionStatus.ONGOING);
        return query.getResultList();
    }

    @Override
    public List<Session> findFinishedSessionByInstructorId(Long sessionId) {
        TypedQuery<Session> query = entityManager.createQuery("SELECT s from Session s WHERE s.instructor.userId = :instructor_id AND s.sessionStatus = :sessionStatus", Session.class);
        query.setParameter("instructor_id", sessionId);
        query.setParameter("sessionStatus", SessionStatus.FINISHED);

        return query.getResultList();
    }

}

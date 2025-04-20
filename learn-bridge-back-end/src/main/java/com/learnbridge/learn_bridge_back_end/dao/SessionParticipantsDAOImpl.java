package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Learner;
import com.learnbridge.learn_bridge_back_end.entity.Session;
import com.learnbridge.learn_bridge_back_end.entity.SessionParticipants;
import com.learnbridge.learn_bridge_back_end.entity.SessionParticipantsId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class SessionParticipantsDAOImpl implements SessionParticipantsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public SessionParticipants saveSessionParticipant(SessionParticipants participant) {
        entityManager.persist(participant);
        return participant;
    }

    @Override
    @Transactional
    public SessionParticipants updateSessionParticipant(SessionParticipants participant) {
        return entityManager.merge(participant);
    }

    @Override
    @Transactional
    public void deleteSessionParticipant(SessionParticipantsId id) {
        SessionParticipants participant = findSessionParticipantById(id);
        if (participant != null) {
            entityManager.remove(participant);
        }
    }

    @Override
    public SessionParticipants findSessionParticipantById(SessionParticipantsId id) {
        return entityManager.find(SessionParticipants.class, id);
    }

    @Override
    public List<SessionParticipants> findParticipantsBySession(Session session) {
        TypedQuery<SessionParticipants> query = entityManager.createQuery(
                "SELECT sp FROM SessionParticipants sp WHERE sp.session = :session",
                SessionParticipants.class
        );
        query.setParameter("session", session);
        return query.getResultList();
    }

    @Override
    public List<SessionParticipants> findSessionsByLearner(Learner learner) {
        TypedQuery<SessionParticipants> query = entityManager.createQuery(
                "SELECT sp FROM SessionParticipants sp WHERE sp.learner = :learner",
                SessionParticipants.class
        );
        query.setParameter("learner", learner);
        return query.getResultList();
    }
}
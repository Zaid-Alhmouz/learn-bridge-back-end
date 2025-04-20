package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Agreement;
import com.learnbridge.learn_bridge_back_end.entity.Instructor;
import com.learnbridge.learn_bridge_back_end.entity.Session;

import java.util.List;

public interface SessionDAO {

    Session saveSession(Session session);
    Session updateSession(Session session);
    Session findSessionById(Long sessionId);
    void deleteSessionById(Long sessionId);
    List<Session> findAllSessions();
    List<Session> findSessionByInstructorId(Long sessionId);
    List<Session> findByAgreementId(Long AgreementId);

}

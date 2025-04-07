package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Session;

import java.util.List;

public interface SessionDAO {

    void saveSession(Session session);
    void updateSession(Session session);
    Session findSessionById(Long sessionId);
    void deleteSessionById(Long sessionId);
    List<Session> findAllSessions();


}

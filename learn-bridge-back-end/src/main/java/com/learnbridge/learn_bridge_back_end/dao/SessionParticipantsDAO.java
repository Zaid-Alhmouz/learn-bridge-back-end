package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Learner;
import com.learnbridge.learn_bridge_back_end.entity.Session;
import com.learnbridge.learn_bridge_back_end.entity.SessionParticipants;
import com.learnbridge.learn_bridge_back_end.entity.SessionParticipantsId;

import java.util.List;

public interface SessionParticipantsDAO {
    SessionParticipants saveSessionParticipant(SessionParticipants participant);
    SessionParticipants updateSessionParticipant(SessionParticipants participant);
    void deleteSessionParticipant(SessionParticipantsId id);
    SessionParticipants findSessionParticipantById(SessionParticipantsId id);
    List<SessionParticipants> findParticipantsBySession(Session session);
    List<SessionParticipants> findSessionsByLearner(Learner learner);
}
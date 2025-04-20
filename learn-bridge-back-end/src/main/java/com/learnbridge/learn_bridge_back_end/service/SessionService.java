package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.*;
import com.learnbridge.learn_bridge_back_end.dto.SessionDTO;
import com.learnbridge.learn_bridge_back_end.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {

    @Autowired
    private SessionDAO sessionDAO;

    @Autowired
    private ChatDAO chatDAO;

    @Autowired
    private AgreementDAO agreementDAO;

    @Autowired
    private SessionParticipantsDAO sessionParticipantsDAO;

    @Autowired
    private PaymentInfoDAO paymentInfoDAO; // needs adjustments

    @Autowired
    private CardDAO cardDAO;

    @Transactional
    public SessionDTO createSessionFromAgreement(Long agreementId) {
        Agreement agreement = agreementDAO.findAgreementById(agreementId);
        if (agreement == null) {
            throw new RuntimeException("Agreement not found with id: " + agreementId);
        }

//        Learner learner = agreement.getLearner();
//        if (learner == null) {
//            throw new RuntimeException("Learner not found with id: " + agreementId);
//        }

        // In a real implementation, we would check payment here
        // For now, we'll assume payment is successful

        // mock card
        Card card = cardDAO.findCardByUserId(agreement.getLearner().getLearnerId());

        // create payment info record (mock)
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setAmount(agreement.getPost().getPrice());

        paymentInfo.setCard(card);
        paymentInfo.setPaymentDate(LocalDate.now());
        paymentInfo.setUser(agreement.getLearner().getUser());
        // set other payment details as needed
        PaymentInfo savedPayment = paymentInfoDAO.savePaymentInfo(paymentInfo);

        // create session
        Session session = new Session();
        session.setTransaction(savedPayment);
        session.setInstructor(agreement.getInstructor().getUser());
        session.setAgreement(agreement);
        session.setSessionStatus(SessionStatus.ONGOING);
        session.setParticipants(new HashSet<>());
        session.setChats(new HashSet<>());

        Session savedSession = sessionDAO.saveSession(session);

        // create chat for the session
        Chat chat = new Chat();
        chat.setSession(savedSession);
        chat.setMessages(new HashSet<>());

        Chat savedChat = chatDAO.saveChat(chat);

        // add session participants
        SessionParticipants learnerParticipant = new SessionParticipants(
                savedSession.getSessionId(),
                agreement.getLearner().getLearnerId()
        );

        sessionParticipantsDAO.saveSessionParticipant(learnerParticipant);

        return convertToDTO(savedSession);
    }



    public SessionDTO getSessionById(Long sessionId) {
        Session session = sessionDAO.findSessionById(sessionId);
        if (session == null) {
            throw new RuntimeException("Session not found with id: " + sessionId);
        }
        return convertToDTO(session);
    }



    public List<SessionDTO> getSessionsByInstructor(Long instructorId) {


        List<Session> sessions = sessionDAO.findSessionByInstructorId(instructorId);
        return sessions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SessionDTO> getSessionsByAgreement(Long agreementId) {
        Agreement agreement = agreementDAO.findAgreementById(agreementId);
        if (agreement == null) {
            throw new RuntimeException("Agreement not found with id: " + agreementId);
        }

        List<Session> sessions = sessionDAO.findByAgreementId(agreementId);
        return sessions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SessionDTO updateSessionStatus(Long sessionId, SessionStatus status) {
        Session session = sessionDAO.findSessionById(sessionId);
        if (session == null) {
            throw new RuntimeException("Session not found with id: " + sessionId);
        }

        session.setSessionStatus(status);
        Session updatedSession = sessionDAO.updateSession(session);

        return convertToDTO(updatedSession);
    }

    private SessionDTO convertToDTO(Session session) {
        SessionDTO dto = new SessionDTO();
        dto.setSessionId(session.getSessionId());
        dto.setInstructorId(session.getInstructor().getId());
        dto.setAgreementId(session.getAgreement().getAgreementId());
        dto.setSessionStatus(session.getSessionStatus().toString());
        return dto;
    }
}
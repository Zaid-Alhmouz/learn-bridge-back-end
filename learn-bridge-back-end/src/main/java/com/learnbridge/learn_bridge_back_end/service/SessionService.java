package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.*;
import com.learnbridge.learn_bridge_back_end.dto.SessionDTO;
import com.learnbridge.learn_bridge_back_end.entity.*;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Autowired
    private StripeService stripeService;

    @Autowired
    NotificationService notificationService;




    @Transactional
    public SessionDTO createSessionFromInstructorAgreement(Long agreementId) throws StripeException {
        Agreement agreement = agreementDAO.findAgreementById(agreementId);
        if (agreement == null) {
            throw new RuntimeException("Agreement not found with id: " + agreementId);
        }


        // 1. Compute amount in cents
        long amountCents = agreement.getPost().getPrice()
                .multiply(new BigDecimal(100))
                .longValue();

        // 2. Lookup default PaymentMethod ID from your Card entity
        Card learnerCard = cardDAO.findDefaultCardByUserId(agreement.getLearner().getLearnerId());
        String paymentMethodId = learnerCard.getStripePaymentMethodId();
        // <— you’ll need to store this when saving cards

        // 3. Hold funds on learner’s card
        PaymentIntent pi = stripeService.createAuthorization(
                amountCents, "usd", paymentMethodId
        );

        // 4. Persist PaymentInfo
        PaymentInfo info = new PaymentInfo();
        info.setUser(agreement.getLearner().getUser());
        info.setCard(learnerCard);
        info.setPaymentDate(LocalDate.now());
        info.setAmount(agreement.getPost().getPrice());
        info.setStripePaymentIntentId(pi.getId());
        paymentInfoDAO.savePaymentInfo(info);

        // 5. Notify learner of hold
        notificationService.sendHoldNotification(
                agreement.getLearner().getUser(),
                agreement.getPost().getPrice(),
                pi.getId()
        );
        // create session
        Session session = new Session();
        session.setTransaction(info);
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



    @Transactional
    public SessionDTO createSessionFromLearnerAgreement(Long agreementId) throws StripeException {
        Agreement ag = agreementDAO.findAgreementById(agreementId);
        if (ag == null) {
            throw new RuntimeException("Agreement not found: " + agreementId);
        }

        long amountCents = ag.getPrice().multiply(new BigDecimal(100)).longValue();

        Card learnerCard = cardDAO.findDefaultCardByUserId(ag.getLearner().getLearnerId());
        String paymentMethodId = learnerCard.getStripePaymentMethodId();

        PaymentIntent pi = stripeService.createAuthorization(
                amountCents, "usd", paymentMethodId
        );

        PaymentInfo info = new PaymentInfo();
        info.setUser(ag.getLearner().getUser());
        info.setCard(learnerCard);
        info.setPaymentDate(LocalDate.now());
        info.setAmount(ag.getPrice());
        info.setStripePaymentIntentId(pi.getId());
        paymentInfoDAO.savePaymentInfo(info);

        notificationService.sendHoldNotification(
                ag.getLearner().getUser(),
                ag.getPrice(),
                pi.getId()
        );

        // session
        Session sess = new Session();
        sess.setTransaction(info);
        sess.setInstructor(ag.getInstructor().getUser());
        sess.setAgreement(ag);
        sess.setSessionStatus(SessionStatus.ONGOING);
        sess.setChats(new HashSet<>());
        sess.setParticipants(new HashSet<>());

        Session saved = sessionDAO.saveSession(sess);

        // chat container
        Chat chat = new Chat();
        chat.setSession(saved);
        chat.setMessages(new HashSet<>());
        chatDAO.saveChat(chat);

        // learner as participant
        SessionParticipants part = new SessionParticipants(saved.getSessionId(),
                ag.getLearner().getLearnerId());
        sessionParticipantsDAO.saveSessionParticipant(part);

        return convertToDTO(saved);
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

    public List<SessionDTO> getSessionsByLearner(Long learnerId) {


        List<Session> sessions = sessionDAO.findSessionsByParticipantId(learnerId);
        return sessions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



    // get session history based on user role
    public List<Session> getUserSessions(User user) {
        if (user == null || user.getUserRole() == null) {
            throw new IllegalArgumentException("User or UserRole is null");
        }

        switch (user.getUserRole()) {
            case INSTRUCTOR:
                return sessionDAO.findSessionByInstructorId(user.getId());
            case LEARNER:
                return sessionDAO.findSessionsByParticipantId(user.getId());
            default:
                throw new UnsupportedOperationException("Unsupported user role: " + user.getUserRole());
        }
    }

    public List<Session> getActiveUserSessions(User user) {
        if (user == null || user.getUserRole() == null) {
            throw new IllegalArgumentException("User or UserRole is null");
        }

        switch (user.getUserRole()) {
            case INSTRUCTOR:
                return sessionDAO.findOngoingSessionByInstructorId(user.getId());
            case LEARNER:
                return sessionDAO.findOngoingSessionsByParticipantId(user.getId());
            default:
                throw new UnsupportedOperationException("Unsupported user role: " + user.getUserRole());
        }
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

    @Transactional
    public SessionDTO cancelSession(Long sessionId, Long cancellerId) {

        Session sessionToBeCancelled = sessionDAO.findSessionById(sessionId);

        sessionToBeCancelled.setSessionStatus(SessionStatus.CANCELLED);
        sessionToBeCancelled.setCancelledById(cancellerId);

        sessionDAO.updateSession(sessionToBeCancelled);

        return convertToDTO(sessionToBeCancelled);
    }

    @Transactional
    public SessionDTO finishSession(Long sessionId, Long finisherId) {
        Session sessionToBeFinished = sessionDAO.findSessionById(sessionId);
        if (sessionToBeFinished == null) {
            throw new RuntimeException("Session not found with id: " + sessionId);
        }
        sessionToBeFinished.setSessionStatus(SessionStatus.FINISHED);
        sessionToBeFinished.setFinishedById(finisherId);
        sessionDAO.updateSession(sessionToBeFinished);
        return convertToDTO(sessionToBeFinished);
    }


    private SessionDTO convertToDTO(Session session) {
        SessionDTO dto = new SessionDTO();
        dto.setSessionId(session.getSessionId());
        dto.setInstructorId(session.getInstructor().getId());
        dto.setAgreementId(session.getAgreement().getAgreementId());
        dto.setSessionStatus(session.getSessionStatus());
        return dto;
    }
}
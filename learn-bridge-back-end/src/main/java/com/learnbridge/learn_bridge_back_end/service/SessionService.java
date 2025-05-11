package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.*;
import com.learnbridge.learn_bridge_back_end.dto.SessionDTO;
import com.learnbridge.learn_bridge_back_end.entity.*;
import com.learnbridge.learn_bridge_back_end.entity.Card;
import com.learnbridge.learn_bridge_back_end.util.SessionMapper;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {

    @Autowired private SessionDAO sessionDAO;
    @Autowired private ChatDAO chatDAO;
    @Autowired private SessionParticipantsDAO participantsDAO;
    @Autowired private AgreementDAO agreementDAO;
    @Autowired private PaymentInfoDAO paymentInfoDAO;
    @Autowired private CardDAO cardDAO;
    @Autowired private StripeService stripeService;
    @Autowired private NotificationService notificationService;

    /**
     * Creates a new session from an existing agreement:
     *  - Authorizes funds on learner's card
     *  - Persists PaymentInfo
     *  - Creates Session, Chat, and participants
     */
    @Transactional
    public SessionDTO createSessionFromInstructorAgreement(Long agreementId) throws StripeException {
        Agreement agreement = agreementDAO.findAgreementById(agreementId);
        if (agreement == null) {
            throw new IllegalArgumentException("Agreement not found: " + agreementId);
        }

        // Authorize payment
        long amountCents = agreement.getPrice().multiply(new BigDecimal(100)).longValue();
        Card learnerCard = cardDAO.findDefaultCardByUserId(agreement.getLearner().getLearnerId());
        String customerId = agreement.getLearner().getUser().getStripeCustomerId();
        String paymentMethodId = learnerCard.getStripePaymentMethodId();
        PaymentIntent pi = stripeService.authorizePayment(amountCents, "usd", customerId, paymentMethodId);

        // Persist payment info
        PaymentInfo info = new PaymentInfo();
        info.setUser(agreement.getLearner().getUser());
        info.setCard(learnerCard);
        info.setPaymentDate(LocalDate.now());
        info.setAmount(agreement.getPrice());
        info.setStripePaymentIntentId(pi.getId());
        paymentInfoDAO.savePaymentInfo(info);

        // Create session
        Session session = new Session();
        session.setAgreement(agreement);
        session.setTransaction(info);
        session.setSessionStatus(SessionStatus.ONGOING);
        session.setInstructor(agreement.getInstructor().getUser());
        session.setChats(new HashSet<>());
        session.setParticipants(new HashSet<>());
        Session saved = sessionDAO.saveSession(session);

        // Create chat
        Chat chat = new Chat();
        chat.setSession(saved);
        chat.setMessages(new HashSet<>());
        chatDAO.saveChat(chat);

        // Add only the learner as a participant
        SessionParticipants learnerPart = new SessionParticipants(
                saved.getSessionId(),
                agreement.getLearner().getLearnerId()
        );
        participantsDAO.saveSessionParticipant(learnerPart);

        // Notify learner of hold
        notificationService.sendHoldNotification(
                agreement.getLearner().getUser(),
                agreement.getPrice(),
                pi.getId()
        );

        return SessionMapper.toSessionDTO(saved);
    }


    /**
     * Captures authorized funds, transfers to instructor, and marks session finished
     */
    @Transactional
    public SessionDTO finishSession(Long sessionId, Long finisherId) throws StripeException {
        Session session = sessionDAO.findSessionById(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }

        PaymentInfo info = session.getTransaction();

        // Capture the payment
        PaymentIntent captured = stripeService.capturePayment(info.getStripePaymentIntentId());
        info.setCaptured(true);

        // Retrieve the latest charge ID from the PaymentIntent
        String latestChargeId = captured.getLatestCharge();
        if (latestChargeId != null) {
            // Retrieve the Charge object using the latest charge ID
            Charge charge = Charge.retrieve(latestChargeId);
            info.setStripeChargeId(charge.getId());
        } else {
            // Handle the case where there is no associated charge
            throw new IllegalStateException("No charge associated with the PaymentIntent.");
        }

        // Transfer funds to the instructor
        long amountCents = info.getAmount().multiply(new BigDecimal(100)).longValue();
        String instructorAccount = session.getAgreement().getInstructor().getUser().getStripeCustomerId();
        Transfer transfer = stripeService.transferToInstructor(amountCents, "usd", instructorAccount);
        info.setStripeTransferId(transfer.getId());

        paymentInfoDAO.updatePaymentInfo(info);

        // Update session status
        session.setSessionStatus(SessionStatus.FINISHED);
        session.setFinishedById(finisherId);
        Session updated = sessionDAO.updateSession(session);

        // Notify instructor of the transfer
        notificationService.sendTransferNotification(
                session.getInstructor(), info.getAmount(), info.getStripeChargeId()
        );

        return convertToDTO(updated);
    }

    /**
     * Cancels session and releases hold
     */
    @Transactional
    public SessionDTO cancelSession(Long sessionId, Long cancellerId) throws StripeException {
        Session session = sessionDAO.findSessionById(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }

        PaymentInfo info = session.getTransaction();
        stripeService.cancelAuthorization(info.getStripePaymentIntentId());
        info.setRefunded(true);
        paymentInfoDAO.updatePaymentInfo(info);

        session.setSessionStatus(SessionStatus.CANCELLED);
        session.setCancelledById(cancellerId);
        Session updated = sessionDAO.updateSession(session);

        notificationService.sendCancelNotification(
                session.getAgreement().getLearner().getUser(), info.getAmount(), info.getStripePaymentIntentId()
        );

        return convertToDTO(updated);
    }

    /**
     * Issues a refund for a completed session
     */
    @Transactional
    public Refund refundSession(Long sessionId, BigDecimal amount) throws StripeException {
        Session session = sessionDAO.findSessionById(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }

        PaymentInfo info = session.getTransaction();
        Long refundAmount = amount != null ? amount.multiply(new BigDecimal(100)).longValue() : null;
        Refund refund = stripeService.refundPayment(info.getStripeChargeId(), refundAmount);
        info.setStripeRefundId(refund.getId());
        info.setRefunded(true);
        paymentInfoDAO.updatePaymentInfo(info);

        notificationService.sendRefundNotification(
                session.getAgreement().getLearner().getUser(), amount != null ? amount : info.getAmount(), refund.getId()
        );

        return refund;
    }


    @Transactional
    public SessionDTO createSessionFromLearnerAgreement(Long agreementId) throws StripeException {
        // 1. Load agreement
        Agreement ag = agreementDAO.findAgreementById(agreementId);
        if (ag == null) {
            throw new IllegalArgumentException("Agreement not found: " + agreementId);
        }

        // 2. Compute amount in cents
        BigDecimal price = ag.getPrice();
        long amountCents = price.multiply(new BigDecimal(100)).longValue();

        // 3. Retrieve learner's default card and Stripe customer ID
        Card learnerCard = cardDAO.findDefaultCardByUserId(ag.getLearner().getLearnerId());
        String paymentMethodId = learnerCard.getStripePaymentMethodId();
        String customerId = ag.getLearner().getUser().getStripeCustomerId();
        if (customerId == null) {
            throw new IllegalStateException("Learner has no Stripe customer ID");
        }

        // 4. Authorize (hold) funds on the learner's card
        PaymentIntent pi = stripeService.authorizePayment(
                amountCents,
                "usd",
                customerId,
                paymentMethodId
        );

        // 5. Persist PaymentInfo
        PaymentInfo info = new PaymentInfo();
        info.setUser(ag.getLearner().getUser());
        info.setCard(learnerCard);
        info.setPaymentDate(LocalDate.now());
        info.setAmount(price);
        info.setStripePaymentIntentId(pi.getId());
        paymentInfoDAO.savePaymentInfo(info);

        // 6. Notify learner of hold
        notificationService.sendHoldNotification(
                ag.getLearner().getUser(),
                price,
                pi.getId()
        );

        // 7. Create Session
        Session sess = new Session();
        sess.setTransaction(info);
        sess.setInstructor(ag.getInstructor().getUser());
        sess.setAgreement(ag);
        sess.setSessionStatus(SessionStatus.ONGOING);
        sess.setChats(new HashSet<>());
        sess.setParticipants(new HashSet<>());

        Session saved = sessionDAO.saveSession(sess);

        // 8. Create initial Chat
        Chat chat = new Chat();
        chat.setSession(saved);
        chat.setMessages(new HashSet<>());
        chatDAO.saveChat(chat);

        // 9. Add participants: learner only (instructor assumed added elsewhere if needed)
        SessionParticipants learnerPart = new SessionParticipants(
                saved.getSessionId(),
                ag.getLearner().getLearnerId()
        );
        participantsDAO.saveSessionParticipant(learnerPart);

        // 10. Return DTO
        return SessionMapper.toSessionDTO(saved);
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


    private SessionDTO convertToDTO(Session session) {
        SessionDTO dto = new SessionDTO();
        dto.setSessionId(session.getSessionId());
        dto.setInstructorId(session.getInstructor().getId());
        dto.setAgreementId(session.getAgreement().getAgreementId());
        dto.setSessionStatus(session.getSessionStatus());
        return dto;
    }
}

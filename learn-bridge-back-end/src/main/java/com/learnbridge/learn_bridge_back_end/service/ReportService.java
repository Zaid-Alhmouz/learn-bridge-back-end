package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.*;
import com.learnbridge.learn_bridge_back_end.dto.ReportDTO;
import com.learnbridge.learn_bridge_back_end.dto.UserDTO;
import com.learnbridge.learn_bridge_back_end.entity.*;
import com.learnbridge.learn_bridge_back_end.util.ReportMapper;
import com.learnbridge.learn_bridge_back_end.util.UserMapper;
import com.stripe.exception.StripeException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ReportService {

    @Autowired
    private StripeService stripeService;

    @Autowired
    private ReportDAO reportDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    LearnerDAO learnerDAO;

    @Autowired
    InstructorDAO instructorDAO;

    @Autowired
    SessionDAO sessionDAO;

    @Autowired
    SessionParticipantsDAO sessionParticipantsDAO;

    @Autowired
    ChatDAO chatDAO;

    @Autowired
    private CardDAO cardDAO;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PaymentInfoDAO paymentInfoDAO;

    // get all pending reports for the admin
    public List<ReportDTO> getAllPendingReports()
    {
        List<Report> pendingReports = reportDAO.findPendingReports();
        if (pendingReports == null || pendingReports.isEmpty())
        {
            return new ArrayList<>();
        }
        return ReportMapper.toReportDTOList(pendingReports);
    }

    // resolve report by admin service
    public ReportDTO resolveReport(Long reportId)
    {
        Report reportToBeResolved = reportDAO.findReportByReportId(reportId);
        if(reportToBeResolved == null)
        {
            throw new RuntimeException("Report with id " + reportId + " not found");
        }
        else{
            reportToBeResolved.setReportStatus(ReportStatus.RESOLVED);
            reportDAO.updateReport(reportToBeResolved);
            return ReportMapper.toReportDTO(reportToBeResolved);
        }
    }

//    // delete report by the admin
//    public ReportDTO deleteReport(Long reportId)
//    {
//        Report report = reportDAO.findReportByReportId(reportId);
//        if(report == null)
//        {
//            throw new RuntimeException("Report with id " + reportId + " not found");
//        }
//        reportDAO.deleteReport(reportId);
//        return ReportMapper.toReportDTO(report);
//    }


    // block user by admin
    public UserDTO blockUser(Long reportedUser)
    {
        User userToBeBlocked = userDAO.findUserById(reportedUser);

        if (userToBeBlocked == null)
        {
            throw new RuntimeException("User with id " + reportedUser + " not found");
        }

        userToBeBlocked.setAccountStatus(AccountStatus.BLOCKED);
        userDAO.updateUser(userToBeBlocked);

        return UserMapper.toUserDTO(userToBeBlocked);
    }


    // create a report based on chatId
    public ReportDTO createReportByChat(
            Long reporterId,
            Long chatId,
            String description,
            ReportType reportType
    ) {
        // Fetch chat and session
        Chat chat = chatDAO.findChatById(chatId);
        if (chat == null) {
            throw new RuntimeException("Chat not found for id " + chatId);
        }
        Session session = chat.getSession();
        if (session == null) {
            throw new RuntimeException("Session not found for chat id " + chatId);
        }

        // Fetch reporter
        User reporter = userDAO.findUserById(reporterId);
        if (reporter == null) {
            throw new RuntimeException("Reporter not found for id " + reporterId);
        }

        // Determine reported user based on reporter role
        User reportedUser;
        UserRole role = reporter.getUserRole();
        if (role == UserRole.LEARNER) {
            // Learner reporting: reported is the instructor
            reportedUser = userDAO.findUserById(session.getInstructor().getId());
            if (reportedUser == null) {
                throw new RuntimeException("Instructor user not found for session " + session.getSessionId());
            }
        } else if (role == UserRole.INSTRUCTOR) {
            // Instructor reporting: reported is the learner participant
            List<SessionParticipants> participants = sessionParticipantsDAO.findParticipantsBySession(session);
            if (participants.isEmpty()) {
                throw new RuntimeException("No learner found in session " + session.getSessionId());
            }
            Long learnerId = participants.get(0).getLearnerId();
            Learner learner = learnerDAO.findLearnerById(learnerId);
            if (learner == null || learner.getUser() == null) {
                throw new RuntimeException("Learner user not found for learnerId " + learnerId);
            }
            reportedUser = learner.getUser();
        } else {
            throw new RuntimeException("Invalid user role for reporting: " + role);
        }

        // Build and save report
        Report report = new Report();
        report.setSession(session);
        report.setReporter(reporter);
        report.setReportedUser(reportedUser);
        report.setDescription(description);
        report.setReportType(reportType);
        report.setReportStatus(ReportStatus.PENDING);
        reportDAO.saveReport(report);

        return ReportMapper.toReportDTO(report);
    }


    @Transactional
    public void resolveBySessionId(Long sessionId) {
        // fetch all reports on that session (usually just one pending)
        List<Report> reports = reportDAO.findReportsBySessionId(sessionId);
        for (Report r : reports) {
            if (r.getReportStatus() == ReportStatus.PENDING) {
                r.setReportStatus(ReportStatus.RESOLVED);
                reportDAO.updateReport(r);
            }
        }
    }



    public Report findOrThrow(Long id) {
        Report r = reportDAO.findReportByReportId(id);
        if (r == null) throw new EntityNotFoundException("Report not found: " + id);
        return r;
    }

    @Transactional
    public void save(Report report) {
        reportDAO.updateReport(report);
    }

    @Transactional
    public ReportDTO deleteReport(Long id) {
        Report r = findOrThrow(id);
        ReportDTO dto = ReportMapper.toReportDTO(r);
        reportDAO.deleteReport(id);
        return dto;
    }

    public ReportDTO toDTO(Report r) {
        return ReportMapper.toReportDTO(r);
    }


    @Transactional
    public ReportDTO transferOrMock(Long reportId) throws StripeException {
        Report r = findOrThrow(reportId);

        PaymentInfo info = r.getSession().getTransaction();
        Session session = sessionDAO.findSessionById(r.getSession().getSessionId());
        long cents = info.getAmount().multiply(new BigDecimal(100)).longValue();
        String currency = "usd";  // or pull from your DTO

        // mock if test-mode, otherwise real transfer
        String transferId;
        if (stripeService.isTestMode()) {
            transferId = "test_xfer_" + UUID.randomUUID();
        } else {
            Instructor inst = instructorDAO.findInstructorById(r.getSession().getInstructor().getId());
            String destAcct = inst.getStripeAccountId();
            transferId = stripeService.transferToInstructor(cents, currency, destAcct).getId();
        }

        // persist the dummy/real transfer ID
        info.setStripeTransferId(transferId);
        r.setReportStatus(ReportStatus.RESOLVED);
        reportDAO.updateReport(r);

        PaymentInfo instructorPaymentInfo = new PaymentInfo();
        instructorPaymentInfo.setUser(session.getInstructor());
        instructorPaymentInfo.setCard(cardDAO.findCardByUserId(session.getInstructor().getId()));
        instructorPaymentInfo.setPaymentDate(LocalDate.now());
        instructorPaymentInfo.setAmount(session.getTransaction().getAmount());
        instructorPaymentInfo.setStripeChargeId(session.getTransaction().getStripeChargeId());

        paymentInfoDAO.savePaymentInfo(instructorPaymentInfo);



        // send transfer notification to instructor
        notificationService.sendTransferNotification(
                r.getSession().getInstructor(),
                info.getAmount(),
                transferId   // or info.getStripeTransferId()
        );

        // return updated DTO
        return toDTO(r);
    }



    @Transactional
    public Report refundOrMock(Long reportId) throws StripeException {
        Report report = findOrThrow(reportId);
        PaymentInfo info = report.getSession().getTransaction();

        // 1) mock or real refund
        String refundId;
        if (stripeService.isTestMode()) {
            refundId = "test_refund_" + UUID.randomUUID();
        } else if (info.getStripeChargeId() != null) {
            // real refund against a Charge
            refundId = stripeService.refundPayment(info.getStripeChargeId(), null).getId();
        } else {
            // real cancel of an authorization
            stripeService.cancelAuthorization(info.getStripePaymentIntentId());
            refundId = null; // or some sentinel
        }

        // 2) persist the refund ID (if any)
        info.setStripeRefundId(refundId);
        paymentInfoDAO.updatePaymentInfo(info);

        // 3) mark and persist the report
        report.setReportStatus(ReportStatus.RESOLVED);
        reportDAO.updateReport(report);

        return report;
    }
}




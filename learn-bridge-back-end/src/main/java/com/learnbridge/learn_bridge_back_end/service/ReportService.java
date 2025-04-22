package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.*;
import com.learnbridge.learn_bridge_back_end.dto.ReportDTO;
import com.learnbridge.learn_bridge_back_end.dto.UserDTO;
import com.learnbridge.learn_bridge_back_end.entity.*;
import com.learnbridge.learn_bridge_back_end.util.ReportMapper;
import com.learnbridge.learn_bridge_back_end.util.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    ReportDAO reportDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    LearnerDAO learnerDAO;

    @Autowired
    InstructorDAO instructorDAO;

    @Autowired
    SessionDAO sessionDAO;

    // get all pending reports for the admin
    public List<ReportDTO> getAllPendingReports()
    {
        List<Report> pendingReports = reportDAO.findPendingReports();
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

    // delete report by the admin
    public ReportDTO deleteReport(Long reportId)
    {
        Report report = reportDAO.findReportByReportId(reportId);
        if(report == null)
        {
            throw new RuntimeException("Report with id " + reportId + " not found");
        }
        reportDAO.deleteReport(reportId);
        return ReportMapper.toReportDTO(report);
    }


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

    public ReportDTO createReport(Long reporterId, Long reportedId, Long relatedSessionId, String description, ReportType reportType)
    {
        User reporter     = userDAO.findUserById(reporterId);
        User reportedUser = userDAO.findUserById(reportedId);
        Session session   = sessionDAO.findSessionById(relatedSessionId);

        if (reporter == null || reportedUser == null || session == null) {
            throw new RuntimeException("Invalid reporter, reportedUser or session ID.");
        }

        Report report = new Report();
        report.setReporter(reporter);
        report.setReportedUser(reportedUser);
        report.setSession(session);
        report.setDescription(description);
        report.setReportType(reportType);
        report.setReportStatus(ReportStatus.PENDING);
        report.setCreationDate(LocalDateTime.now());

        reportDAO.saveReport(report);

        return ReportMapper.toReportDTO(report);
    }

}

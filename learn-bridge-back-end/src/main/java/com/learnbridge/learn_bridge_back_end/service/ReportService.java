package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.InstructorDAO;
import com.learnbridge.learn_bridge_back_end.dao.LearnerDAO;
import com.learnbridge.learn_bridge_back_end.dao.ReportDAO;
import com.learnbridge.learn_bridge_back_end.dao.UserDAO;
import com.learnbridge.learn_bridge_back_end.dto.ReportDTO;
import com.learnbridge.learn_bridge_back_end.dto.UserDTO;
import com.learnbridge.learn_bridge_back_end.entity.AccountStatus;
import com.learnbridge.learn_bridge_back_end.entity.Report;
import com.learnbridge.learn_bridge_back_end.entity.ReportStatus;
import com.learnbridge.learn_bridge_back_end.entity.User;
import com.learnbridge.learn_bridge_back_end.util.ReportMapper;
import com.learnbridge.learn_bridge_back_end.util.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


}

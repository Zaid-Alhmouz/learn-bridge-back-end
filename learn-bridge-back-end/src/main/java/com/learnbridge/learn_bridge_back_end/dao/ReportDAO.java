package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Report;

import java.util.List;

public interface ReportDAO {

    void saveReport(Report report);
    void updateReport(Report report);
    void deleteReport(Long reportId);
    Report findReportByReportId(Long reportId);
    List<Report> findReportsBySessionId(Long sessionId);
    List<Report> findAllReports();
    List<Report> findPendingReports();
    List<Report> findResolvedReports();

}

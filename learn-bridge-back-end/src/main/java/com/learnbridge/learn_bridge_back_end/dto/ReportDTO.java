package com.learnbridge.learn_bridge_back_end.dto;

import com.learnbridge.learn_bridge_back_end.entity.Report;
import com.learnbridge.learn_bridge_back_end.entity.ReportStatus;
import com.learnbridge.learn_bridge_back_end.entity.ReportType;

import java.time.LocalDateTime;

public class ReportDTO {

    private Long reportId;
    private Long relatedSessionId;
    private ReportStatus reportStatus;
    private Long reportedById;
    private Long reportedUserId;
    private String description;
    private LocalDateTime reportDate;
    private ReportType reportType;

    public ReportDTO() {}

    public ReportDTO(Report report) {
        this.reportId = report.getReportId();
        this.relatedSessionId = report.getSession().getSessionId();
        this.reportStatus = report.getReportStatus();
        this.reportedById = report.getReporter().getId();
        this.reportedUserId = report.getReportedUser().getId();
        this.description = report.getDescription();
        this.reportDate = report.getCreationDate();
        this.reportType = report.getReportType();
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Long getRelatedSessionId() {
        return relatedSessionId;
    }

    public void setRelatedSessionId(Long relatedSessionId) {
        this.relatedSessionId = relatedSessionId;
    }

    public ReportStatus getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(ReportStatus reportStatus) {
        this.reportStatus = reportStatus;
    }

    public Long getReportedById() {
        return reportedById;
    }

    public void setReportedById(Long reportedById) {
        this.reportedById = reportedById;
    }

    public Long getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(Long reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }


    @Override
    public String toString() {
        return "ReportDTO{" +
                "reportId=" + reportId +
                ", relatedSessionId=" + relatedSessionId +
                ", reportStatus=" + reportStatus +
                ", reportedById=" + reportedById +
                ", reportedUserId=" + reportedUserId +
                ", description='" + description + '\'' +
                ", reportDate=" + reportDate +
                ", reportType=" + reportType +
                '}';
    }
}

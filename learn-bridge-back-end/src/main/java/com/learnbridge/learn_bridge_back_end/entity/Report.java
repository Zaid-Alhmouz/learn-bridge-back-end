package com.learnbridge.learn_bridge_back_end.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_session_id", referencedColumnName = "session_id")
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by", referencedColumnName = "user_id")
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user", referencedColumnName = "user_id")
    private User reportedUser;

    @Column(name = "description")
    private String description;


    @Enumerated(EnumType.STRING)
    @Column(name = "report_status")
    private ReportStatus reportStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type")
    private ReportType reportType;


    @Column(name = "creation_date")
    private LocalDateTime creationDate;


    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
    }

    public Long getReportId() {
        return reportId;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public User getReportedUser() {
        return reportedUser;
    }

    public void setReportedUser(User reportedUser) {
        this.reportedUser = reportedUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public ReportStatus getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(ReportStatus reportStatus) {
        this.reportStatus = reportStatus;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }



    @Override
    public String toString() {
        return "Report{" +
                "reportId=" + reportId +
                ", session=" + session +
                ", reporter=" + reporter +
                ", reportedUser=" + reportedUser +
                ", description='" + description + '\'' +
                ", reportStatus=" + reportStatus +
                ", reportType=" + reportType +
                ", creationDate=" + creationDate +
                '}';
    }
}

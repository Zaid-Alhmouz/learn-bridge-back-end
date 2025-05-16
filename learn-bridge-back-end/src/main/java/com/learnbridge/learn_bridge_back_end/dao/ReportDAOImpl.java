package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Report;
import com.learnbridge.learn_bridge_back_end.entity.ReportStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ReportDAOImpl implements ReportDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void saveReport(Report report) {
        entityManager.persist(report);
    }

    @Override
    @Transactional
    public void updateReport(Report report) {
        entityManager.merge(report);
    }

    @Override
    @Transactional
    public void deleteReport(Long reportId) {
        Report report = entityManager.find(Report.class, reportId);
        entityManager.remove(entityManager.contains(report) ? report : entityManager.merge(report));
    }

    @Override
    public Report findReportByReportId(Long reportId) {
        return entityManager.find(Report.class, reportId);
    }

    @Override
    public List<Report> findReportsBySessionId(Long sessionId) {
        String jpql = "select r from Report r where r.session.sessionId = :sessionId";
        TypedQuery<Report> query = entityManager.createQuery(jpql, Report.class);
        query.setParameter("sessionId", sessionId);
        return query.getResultList();
    }

    @Override
    public List<Report> findAllReports() {
        String jpql = "select r from Report r";
        TypedQuery<Report> query = entityManager.createQuery(jpql, Report.class);
        return query.getResultList();
    }

    @Override
    public List<Report> findPendingReports() {
        String jpql = "select r from Report r where r.reportStatus = :status";
        TypedQuery<Report> query = entityManager.createQuery(jpql, Report.class);
        query.setParameter("status", ReportStatus.PENDING);
        return query.getResultList();
    }

    @Override
    public List<Report> findResolvedReports() {
        String jpql = "select r from Report r where r.reportStatus = :status";
        TypedQuery<Report> query = entityManager.createQuery(jpql, Report.class);
        query.setParameter("status", ReportStatus.RESOLVED);
        return query.getResultList();
    }
}

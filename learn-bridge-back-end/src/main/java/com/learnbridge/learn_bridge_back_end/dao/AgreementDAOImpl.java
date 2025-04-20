package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class AgreementDAOImpl implements AgreementDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Agreement saveAgreement(Agreement agreement) {

        entityManager.persist(agreement);
        return agreement;
    }

    @Override
    @Transactional
    public Agreement updateAgreement(Agreement agreement) {
        return entityManager.merge(agreement);
    }

    @Override
    public Agreement findAgreementById(Long agreementId) {
        return entityManager.find(Agreement.class, agreementId);
    }

    @Override
    @Transactional
    public void deleteAgreement(Long agreementId) {

        entityManager.remove(entityManager.contains(agreementId) ? agreementId : entityManager.merge(agreementId));
    }

    @Override
    public List<Agreement> findAllAgreements() {
        return entityManager.createQuery("from Agreement", Agreement.class).getResultList();
    }

    @Override
    public List<Agreement> findAgreementByLearnerId(Long learnerId) {

        TypedQuery<Agreement> query = entityManager.createQuery("SELECT a FROM Agreement a WHERE a.learner.learnerId = :learner_id ", Agreement.class)
                .setParameter("learner_id", learnerId);
        return query.getResultList();
    }

    @Override
    public List<Agreement> findAgreementByInstructorId(Long instructorId) {
        TypedQuery<Agreement> query = entityManager.createQuery(
                "SELECT a FROM Agreement a WHERE a.instructor.instructorId = :instructor_id",
                Agreement.class);
        query.setParameter("instructor_id", instructorId);
        return query.getResultList();
    }

    @Override
    public List<Agreement> findAgreementByPostId(PostId postId) {
        TypedQuery<Agreement> query = entityManager.createQuery(
                "SELECT a FROM Agreement a WHERE a.post.postId = :post_id",
                Agreement.class);
        query.setParameter("post_id", postId);
        return query.getResultList();
    }
}

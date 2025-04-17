package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Agreement;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class AgreementDAOImpl implements AgreementDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void saveAgreement(Agreement agreement) {

        entityManager.persist(agreement);
    }

    @Override
    @Transactional
    public void updateAgreement(Agreement agreement) {

        entityManager.merge(agreement);
    }

    @Override
    public Agreement findAgreement(Long agreementId) {
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
}

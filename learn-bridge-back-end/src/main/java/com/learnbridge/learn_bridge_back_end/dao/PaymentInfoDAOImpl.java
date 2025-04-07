package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.PaymentInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PaymentInfoDAOImpl implements PaymentInfoDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void savePaymentInfo(PaymentInfo paymentInfo) {

        entityManager.persist(paymentInfo);
    }

    @Override
    @Transactional
    public void updatePaymentInfo(PaymentInfo paymentInfo) {

        entityManager.merge(paymentInfo);
    }

    @Override
    public PaymentInfo findPaymentInfoById(Long paymentInfoId) {
        return entityManager.find(PaymentInfo.class, paymentInfoId);
    }

    @Override
    public List<PaymentInfo> findAllPaymentInfos() {
        return entityManager.createQuery("from PaymentInfo", PaymentInfo.class).getResultList();
    }

    @Override
    @Transactional
    public void deletePaymentInfoById(Long paymentInfoId) {

        PaymentInfo paymentInfo = entityManager.find(PaymentInfo.class, paymentInfoId);
        entityManager.remove(entityManager.contains(paymentInfo) ? paymentInfo : entityManager.merge(paymentInfo));
    }
}

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
    public PaymentInfo savePaymentInfo(PaymentInfo paymentInfo) {
        entityManager.persist(paymentInfo);
        return paymentInfo;
    }

    @Override
    @Transactional
    public PaymentInfo updatePaymentInfo(PaymentInfo paymentInfo) {
        return entityManager.merge(paymentInfo);
    }

    @Override
    public PaymentInfo findPaymentInfoById(Long transactionId) {
        return entityManager.find(PaymentInfo.class, transactionId);
    }

    @Override
    @Transactional
    public void deletePaymentInfo(Long transactionId) {
        PaymentInfo paymentInfo = findPaymentInfoById(transactionId);
        if (paymentInfo != null) {
            entityManager.remove(paymentInfo);
        }
    }

    @Override
    public List<PaymentInfo> findAllPaymentInfos() {
        return entityManager.createQuery("from PaymentInfo", PaymentInfo.class).getResultList();
    }
}
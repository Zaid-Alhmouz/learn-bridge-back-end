package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.PaymentInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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


    @Override
    public Optional<PaymentInfo> findByStripeTransferId(String transferId) {
        TypedQuery<PaymentInfo> query = entityManager.createQuery(
                "SELECT p FROM PaymentInfo p WHERE p.stripeTransferId = :transferId", PaymentInfo.class);
        query.setParameter("transferId", transferId);
        List<PaymentInfo> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<PaymentInfo> findByStripeChargeId(String chargeId) {
        TypedQuery<PaymentInfo> query = entityManager.createQuery(
                "SELECT p FROM PaymentInfo p WHERE p.stripeChargeId = :chargeId", PaymentInfo.class);
        query.setParameter("chargeId", chargeId);
        List<PaymentInfo> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<PaymentInfo> findByStripePaymentIntentId(String paymentIntentId) {
        TypedQuery<PaymentInfo> query = entityManager.createQuery(
                "SELECT p FROM PaymentInfo p WHERE p.stripePaymentIntentId = :paymentIntentId", PaymentInfo.class);
        query.setParameter("paymentIntentId", paymentIntentId);
        List<PaymentInfo> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }


}
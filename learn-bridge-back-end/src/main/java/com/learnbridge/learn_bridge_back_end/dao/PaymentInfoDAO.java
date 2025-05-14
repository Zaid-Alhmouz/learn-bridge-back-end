package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentInfoDAO {
    PaymentInfo savePaymentInfo(PaymentInfo paymentInfo);
    PaymentInfo updatePaymentInfo(PaymentInfo paymentInfo);
    PaymentInfo findPaymentInfoById(Long transactionId);
    void deletePaymentInfo(Long transactionId);
    List<PaymentInfo> findAllPaymentInfos();
    List<PaymentInfo> findAllPaymentInfosByUserId(Long userId);

    Optional<PaymentInfo> findByStripePaymentIntentId(String paymentIntentId);

    Optional<PaymentInfo> findByStripeChargeId(String chargeId);

    Optional<PaymentInfo> findByStripeTransferId(String transferId);
}
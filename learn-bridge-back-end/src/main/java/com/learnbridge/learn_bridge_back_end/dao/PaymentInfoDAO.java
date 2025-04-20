package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.PaymentInfo;
import java.util.List;

public interface PaymentInfoDAO {
    PaymentInfo savePaymentInfo(PaymentInfo paymentInfo);
    PaymentInfo updatePaymentInfo(PaymentInfo paymentInfo);
    PaymentInfo findPaymentInfoById(Long transactionId);
    void deletePaymentInfo(Long transactionId);
    List<PaymentInfo> findAllPaymentInfos();
}
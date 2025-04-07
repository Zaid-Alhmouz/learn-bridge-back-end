package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.PaymentInfo;

import java.util.List;

public interface PaymentInfoDAO {
    void savePaymentInfo(PaymentInfo paymentInfo);
    void updatePaymentInfo(PaymentInfo paymentInfo);
    PaymentInfo findPaymentInfoById(Long paymentInfoId);
    List<PaymentInfo> findAllPaymentInfos();
    void deletePaymentInfoById(Long paymentInfoId);

}

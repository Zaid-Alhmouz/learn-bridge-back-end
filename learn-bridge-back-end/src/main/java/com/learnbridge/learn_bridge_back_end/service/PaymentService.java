package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.CardDAO;
import com.learnbridge.learn_bridge_back_end.dao.PaymentInfoDAO;
import com.learnbridge.learn_bridge_back_end.dto.PaymentInfoDTO;
import com.learnbridge.learn_bridge_back_end.entity.PaymentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private CardDAO cardDAO;

    @Autowired
    private PaymentInfoDAO paymentInfoDAO;


    // get all transactions and payments info for user
    public List<PaymentInfoDTO> getAllPaymentInfoByUser(Long userId) {

        List<PaymentInfo> paymentInfoList = paymentInfoDAO.findAllPaymentInfosByUserId(userId);

        if (paymentInfoList == null || paymentInfoList.isEmpty()) {
            return new ArrayList<>();
        }
        else{
            return paymentInfoList.stream().map(this::convertPaymentInfoDTO).collect(Collectors.toList());
        }
    }

    private PaymentInfoDTO convertPaymentInfoDTO(PaymentInfo paymentInfo){
        return new PaymentInfoDTO(paymentInfo);
    }

}

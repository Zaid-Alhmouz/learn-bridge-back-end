package com.learnbridge.learn_bridge_back_end.controller;

import com.learnbridge.learn_bridge_back_end.dto.PaymentInfoDTO;
import com.learnbridge.learn_bridge_back_end.entity.PaymentInfo;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/my-payments-info")
    public ResponseEntity<List<PaymentInfoDTO>> getMyPayments(@AuthenticationPrincipal SecurityUser loggedUser) {

        Long userId = loggedUser.getUser().getId();

        List<PaymentInfoDTO> userPaymentHistory = paymentService.getAllPaymentInfoByUser(userId);

        if (userPaymentHistory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userPaymentHistory);
    }

}

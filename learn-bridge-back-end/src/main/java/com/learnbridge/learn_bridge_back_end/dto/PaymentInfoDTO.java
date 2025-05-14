package com.learnbridge.learn_bridge_back_end.dto;

import com.learnbridge.learn_bridge_back_end.entity.PaymentInfo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentInfoDTO {

    private Long paymentId;
    private Long userId;
    private String cardNumber;
    private LocalDate paymentDate;
    private BigDecimal amount;

    public PaymentInfoDTO() {}

    public PaymentInfoDTO(PaymentInfo paymentInfo) {
        this.paymentId = paymentInfo.getPaymentId();
        this.userId = paymentInfo.getUser().getId();
        this.cardNumber = paymentInfo.getCard().getCardNumber();
        this.paymentDate = paymentInfo.getPaymentDate();
        this.amount = paymentInfo.getAmount();
    }


    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


    @Override
    public String toString() {
        return "PaymentInfoDTO{" +
                "paymentId=" + paymentId +
                ", userId=" + userId +
                ", cardNumber='" + cardNumber + '\'' +
                ", paymentDate=" + paymentDate +
                ", amount=" + amount +
                '}';
    }
}

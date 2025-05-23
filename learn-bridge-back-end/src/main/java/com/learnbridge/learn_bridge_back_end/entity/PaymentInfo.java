package com.learnbridge.learn_bridge_back_end.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment_info")
public class PaymentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "receiver_name")
    private String receiverName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", referencedColumnName = "card_id")
    private Card card;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "amount", nullable = false, precision = 6, scale = 2)
    private BigDecimal amount;

    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;

    @Column(name = "stripe_charge_id")
    private String stripeChargeId;

    @Column(name = "stripe_refund_id")
    private String stripeRefundId;


    @Column(name = "stripe_transfer_id")
    private String stripeTransferId;


    @Column(name = "captured", nullable = false)
    private Boolean captured = false;

    @Column(name = "refunded", nullable = false)
    private Boolean refunded = false;

    // Getters and setters

    public Long getPaymentId() {
        return transactionId;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Card getCard() {
        return card;
    }
    public void setCard(Card card) {
        this.card = card;
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

    public String getStripePaymentIntentId() {
        return stripePaymentIntentId;
    }
    public void setStripePaymentIntentId(String stripePaymentIntentId) {
        this.stripePaymentIntentId = stripePaymentIntentId;
    }

    public String getStripeChargeId() {
        return stripeChargeId;
    }
    public void setStripeChargeId(String stripeChargeId) {
        this.stripeChargeId = stripeChargeId;
    }

    public String getStripeRefundId() {
        return stripeRefundId;
    }
    public void setStripeRefundId(String stripeRefundId) {
        this.stripeRefundId = stripeRefundId;
    }

    public String getStripeTransferId() {
        return stripeTransferId;
    }
    public void setStripeTransferId(String stripeTransferId) {
        this.stripeTransferId = stripeTransferId;
    }

    public Boolean getCaptured() {
        return captured;
    }
    public void setCaptured(Boolean captured) {
        this.captured = captured;
    }

    public Boolean getRefunded() {
        return refunded;
    }
    public void setRefunded(Boolean refunded) {
        this.refunded = refunded;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    @Override
    public String toString() {
        return "PaymentInfo{" +
                "transactionId=" + transactionId +
                ", user=" + user +
                ", senderName='" + senderName + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", card=" + card +
                ", paymentDate=" + paymentDate +
                ", amount=" + amount +
                ", stripePaymentIntentId='" + stripePaymentIntentId + '\'' +
                ", stripeChargeId='" + stripeChargeId + '\'' +
                ", stripeRefundId='" + stripeRefundId + '\'' +
                ", stripeTransferId='" + stripeTransferId + '\'' +
                ", captured=" + captured +
                ", refunded=" + refunded +
                '}';
    }
}

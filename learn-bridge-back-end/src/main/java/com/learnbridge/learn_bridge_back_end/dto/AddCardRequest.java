package com.learnbridge.learn_bridge_back_end.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.learnbridge.learn_bridge_back_end.converter.CustomYearMonthDeserializer;
import com.learnbridge.learn_bridge_back_end.converter.CustomYearMonthSerializer;
import com.learnbridge.learn_bridge_back_end.entity.Card;

import java.time.YearMonth;

public class AddCardRequest {
    // Card details for display / validation only
    private String cardNumber;
    private String holderName;

    @JsonDeserialize(using = CustomYearMonthDeserializer.class)
    @JsonSerialize(using = CustomYearMonthSerializer.class)
    private YearMonth expireDate;


    // that must be sent to the backend to attach to the customer.
    private String paymentMethodId;

    // flags to guide frontend UI
    private boolean isExpired;
    private boolean isDefault;

    public AddCardRequest() {}

    // Constructor for mapping back from entity if needed
    public AddCardRequest(Card card) {
        this.cardNumber   = card.getCardNumber();
        this.holderName   = card.getHolderName();
        this.expireDate   = card.getExpireDate();
        this.isDefault    = card.isDefaultCard();
        this.paymentMethodId = card.getStripePaymentMethodId();
    }



    public String getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getHolderName() {
        return holderName;
    }
    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public YearMonth getExpireDate() {
        return expireDate;
    }
    public void setExpireDate(YearMonth expireDate) {
        this.expireDate = expireDate;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }
    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public boolean isExpired() {
        return isExpired;
    }
    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public boolean isDefault() {
        return isDefault;
    }
    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @Override
    public String toString() {
        return "AddCardRequest{" +
                "cardNumber='" + cardNumber + '\'' +
                ", holderName='" + holderName + '\'' +
                ", expireDate=" + expireDate +
                ", paymentMethodId='" + paymentMethodId + '\'' +
                ", isExpired=" + isExpired +
                ", isDefault=" + isDefault +
                '}';
    }
}

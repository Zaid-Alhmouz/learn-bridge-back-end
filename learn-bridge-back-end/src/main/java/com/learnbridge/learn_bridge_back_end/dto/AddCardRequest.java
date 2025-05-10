package com.learnbridge.learn_bridge_back_end.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.learnbridge.learn_bridge_back_end.converter.CustomYearMonthDeserializer;
import com.learnbridge.learn_bridge_back_end.converter.CustomYearMonthSerializer;

import com.learnbridge.learn_bridge_back_end.entity.Card;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.YearMonth;

/**
 * Data Transfer Object for card operations.
 * Uses a secure token approach (preferred) or raw card details for backward compatibility.
 */
public class AddCardRequest {

//    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^[0-9]{16}$", message = "Card number must be exactly 16 digits")
    private String cardNumber;

//    @NotBlank(message = "Expiry date is required")
    @JsonDeserialize(using = CustomYearMonthDeserializer.class)
    @JsonSerialize(using = CustomYearMonthSerializer.class)
    private YearMonth expireDate;
//
//    @NotBlank(message = "Cardholder name is required")
    private String holderName;

    @JsonProperty("paymentMethodId")
    private String paymentMethodId;

//    @NotBlank(message = "CVC is required")
//    @Size(min = 3, max = 4, message = "CVC must be 3 or 4 digits")
//    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVC must contain only digits")
    private String cvc;

    @JsonProperty("isDefault")
    private boolean isDefault;

    public AddCardRequest() {}


    public AddCardRequest(Card card) {

        this.cardNumber = card.getCardNumber();
        this.expireDate = card.getExpireDate();
        this.holderName = card.getHolderName();
        this.isDefault = card.isDefaultCard();
        this.paymentMethodId = card.getStripePaymentMethodId();

    }

    // Getters and setters
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public YearMonth getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(YearMonth expireDate) {
        this.expireDate = expireDate;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        this.isDefault = aDefault;
    }

    @Override
    public String toString() {
        return "AddCardRequest{" +
                "cardNumber='xxxx-xxxx-xxxx-" + (cardNumber != null ? cardNumber.substring(cardNumber.length() - 4) : "") + '\'' +
                ", expireDate=" + expireDate +
                ", holderName='" + holderName + '\'' +
                ", paymentMethodId='" + (paymentMethodId != null ? "[FILTERED]" : null) + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}

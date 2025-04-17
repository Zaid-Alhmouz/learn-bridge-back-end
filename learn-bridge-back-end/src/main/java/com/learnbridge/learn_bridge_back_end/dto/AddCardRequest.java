package com.learnbridge.learn_bridge_back_end.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.learnbridge.learn_bridge_back_end.converter.CustomYearMonthDeserializer;
import com.learnbridge.learn_bridge_back_end.converter.CustomYearMonthSerializer;
import com.learnbridge.learn_bridge_back_end.entity.Card;

import java.time.YearMonth;

public class AddCardRequest {
    private String cardNumber;
    private String holderName;
    @JsonDeserialize(using = CustomYearMonthDeserializer.class)
    @JsonSerialize(using = CustomYearMonthSerializer.class)
    private YearMonth expireDate;



    public AddCardRequest()
    {

    }

    public AddCardRequest(Card card)
    {
        this.cardNumber = card.getCardNumber();
        this.expireDate = card.getExpireDate();
        this.holderName = card.getHolderName();
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

}

package com.learnbridge.learn_bridge_back_end.dto;

import com.learnbridge.learn_bridge_back_end.entity.Card;
import com.learnbridge.learn_bridge_back_end.entity.CardType;

import java.time.YearMonth;

public class CardDTO {

    private Long cardId;
    private String holderName;
    private String cardNumber;
    private String cardType;
    private String expiryDate;

    public CardDTO() {}

    public CardDTO(Card card)
    {
        this.cardId = card.getCardId();
        this.holderName = card.getHolderName();
        this.cardNumber = card.getCardNumber();
        this.cardType = card.getCardType().toString();
        this.expiryDate = card.getExpireDate().toString();
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}

package com.learnbridge.learn_bridge_back_end.entity;


import com.learnbridge.learn_bridge_back_end.converter.YearMonthAttributeConverter;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(name = "card")
public class Card {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long cardId;


    @Column(name = "card_number", nullable = false, length = 19)
    private String cardNumber;

    @Column(name = "expire_date", nullable = false)
    @Convert(converter = YearMonthAttributeConverter.class)
    private YearMonth expireDate;

    @Column(name = "holder_name", nullable = false, length = 100)
    private String holderName;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false, length = 10)
    private CardType cardType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "default_card")
    private boolean defaultCard;


    @Column(name = "stripe_pm_id", nullable = false, unique = true, length = 255)
    private String stripePaymentMethodId;



    public Long getCardId() { return cardId; }

    public String getCardNumber() { return cardNumber; }

    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public YearMonth getExpireDate() { return expireDate; }

    public void setExpireDate(YearMonth expireDate) { this.expireDate = expireDate; }

    public String getHolderName() { return holderName; }

    public void setHolderName(String holderName) { this.holderName = holderName; }

    public CardType getCardType() { return cardType; }

    public void setCardType(CardType cardType) { this.cardType = cardType; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public boolean isDefaultCard() {
        return defaultCard;
    }

    public void setDefaultCard(boolean defaultCard) {
        this.defaultCard = defaultCard;
    }

    public String getStripePaymentMethodId() {
        return stripePaymentMethodId;
    }

    public void setStripePaymentMethodId(String stripePaymentMethodId) {
        this.stripePaymentMethodId = stripePaymentMethodId;
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardId=" + cardId +
                ", cardNumber='" + cardNumber + '\'' +
                ", expireDate=" + expireDate +
                ", holderName='" + holderName + '\'' +
                ", cardType=" + cardType +
                ", user=" + user +
                ", defaultCard=" + defaultCard +
                ", stripePaymentMethodId='" + stripePaymentMethodId + '\'' +
                '}';
    }
}
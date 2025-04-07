package com.learnbridge.learn_bridge_back_end.entity;



import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long cardId;

    @Column(name = "card_number", nullable = false, unique = true, length = 16)
    private String cardNumber;

    @Column(name = "expire_date", nullable = false)
    private LocalDate expireDate;

    @Column(name = "holder_name", nullable = false, length = 100)
    private String holderName;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false, length = 10)
    private CardType cardType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;



    public Long getCardId() { return cardId; }

    public String getCardNumber() { return cardNumber; }

    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public LocalDate getExpireDate() { return expireDate; }

    public void setExpireDate(LocalDate expireDate) { this.expireDate = expireDate; }

    public String getHolderName() { return holderName; }

    public void setHolderName(String holderName) { this.holderName = holderName; }

    public CardType getCardType() { return cardType; }

    public void setCardType(CardType cardType) { this.cardType = cardType; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    @Override
    public String toString() {
        return "Card{" +
                "cardNumber='" + cardNumber + '\'' +
                ", expireDate=" + expireDate +
                ", holderName='" + holderName + '\'' +
                ", cardType=" + cardType +
                ", cardId=" + cardId +
                '}';
    }
}
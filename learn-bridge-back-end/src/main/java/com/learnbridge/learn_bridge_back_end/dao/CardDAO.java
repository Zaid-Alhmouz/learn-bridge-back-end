package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Card;

import java.time.YearMonth;
import java.util.List;

public interface CardDAO {

    Card saveCard(Card card);
    Card updateCard(Card card);
    void deleteCard(Long cardId);
    List<Card> getAllCards();
    Card findCardById(Long cardId);
    Card findCardByUserId(Long userId);
    List<Card> findAllCardsByUserId(Long userId);
    Card findCardByCardNumber(String cardNumber);
    public Card findDefaultCard();
    public Card findDefaultCardByUserId(Long userId);
    Card findByUserIdAndLast4AndExpireDate(Long userId, String last4, YearMonth expireDate);
    Card findByStripePaymentMethodId(String stripePmId, Long userId);
}

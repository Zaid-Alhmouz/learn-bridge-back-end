package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Card;

import java.util.List;

public interface CardDAO {

    void saveCard(Card card);
    void updateCard(Card card);
    void deleteCard(Long cardId);
    List<Card> getAllCards();
    Card findCardById(Long cardId);
    Card findCardByUserId(Long userId);
    List<Card> findAllCardsByUserId(Long userId);
    Card findCardByCardNumber(String cardNumber);
}

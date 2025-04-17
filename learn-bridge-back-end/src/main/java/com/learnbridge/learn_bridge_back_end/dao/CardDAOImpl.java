package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Card;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CardDAOImpl implements CardDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void saveCard(Card card) {
        entityManager.persist(card);
    }

    @Override
    @Transactional
    public void updateCard(Card card) {
        entityManager.merge(card);
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {

        Card card = entityManager.find(Card.class, cardId);
        entityManager.remove(entityManager.contains(card) ? card : entityManager.merge(card));
    }

    @Override
    public List<Card> getAllCards() {
        return entityManager.createQuery("from Card", Card.class).getResultList();
    }

    @Override
    public Card findCardById(Long cardId) {
        String sqlStatement = "from Card c where c.cardId = :cardId";
        TypedQuery<Card> query = entityManager.createQuery(sqlStatement, Card.class);
        query.setParameter("cardId", cardId);
        Card card = query.getSingleResult();
        return card;
    }

    @Override
    public Card findCardByUserId(Long userId) {
        String sqlStatement = "from Card c where c.user.userId = :userId";
        TypedQuery<Card> query = entityManager.createQuery(sqlStatement, Card.class);
        query.setParameter("userId", userId);
         List<Card> cards = query.getResultList();
        return cards.isEmpty() ? null : cards.get(0);

    }

    @Override
    public List<Card> findAllCardsByUserId(Long userId) {
        String sqlStatement = "from Card c where c.user.userId = :userId";
        TypedQuery<Card> query = entityManager.createQuery(sqlStatement, Card.class);
        query.setParameter("userId", userId);
        List<Card> cards = query.getResultList();
        return cards;
    }

    @Override
    public Card findCardByCardNumber(String cardNumber) {
        String sqlStatement = "from Card c where c.cardNumber = :cardNumber";
        TypedQuery<Card> query = entityManager.createQuery(sqlStatement, Card.class);
        query.setParameter("cardNumber", cardNumber);
        Card card = query.getSingleResult();
        return card;
    }
}

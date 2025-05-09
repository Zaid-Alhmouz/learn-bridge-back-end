package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.CardDAO;
import com.learnbridge.learn_bridge_back_end.dao.UserDAO;
import com.learnbridge.learn_bridge_back_end.dto.AddCardRequest;
import com.learnbridge.learn_bridge_back_end.dto.AddCardResponse;
import com.learnbridge.learn_bridge_back_end.entity.Card;
import com.learnbridge.learn_bridge_back_end.entity.CardType;
import com.learnbridge.learn_bridge_back_end.entity.User;
import com.learnbridge.learn_bridge_back_end.util.CardMapper;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

@Service
public class CardService {

    @Autowired
    private CardDAO cardDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private StripeService stripeService;

    /**
     * Adds a new payment card for a user.
     * Supports both secure tokenized approach (paymentMethodId) and raw card details.
     */
    @Transactional
    public AddCardResponse addCard(AddCardRequest request, Long userId) throws StripeException {
        User user = userDAO.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        Customer customer = stripeService.getOrCreateCustomer(user.getEmail());
        if (!customer.getId().equals(user.getStripeCustomerId())) {
            user.setStripeCustomerId(customer.getId());
            userDAO.updateUser(user);
        }

        PaymentMethod paymentMethod;
        if (request.getPaymentMethodId() != null && !request.getPaymentMethodId().isEmpty()) {
            paymentMethod = stripeService.retrieveAndAttachPaymentMethod(
                    customer.getId(), request.getPaymentMethodId()
            );
        } else {
            // Use raw card details to create and attach a new PaymentMethod
            YearMonth exp = request.getExpireDate();
            paymentMethod = stripeService.createAndAttachCard(
                    customer.getId(),
                    request.getCardNumber(),
                    String.valueOf(exp.getMonthValue()),
                    String.valueOf(exp.getYear()),
                    request.getCvc()
            );
        }

        // Persist Card entity locally
        Card card = new Card();
        card.setUser(user);
        card.setStripePaymentMethodId(paymentMethod.getId());
        card.setHolderName(request.getHolderName());
        card.setExpireDate(YearMonth.of(
                paymentMethod.getCard().getExpYear().intValue(),
                paymentMethod.getCard().getExpMonth().intValue()
        ));
        card.setCardNumber("xxxxxxxxxxxx" + paymentMethod.getCard().getLast4());

        List<Card> existing = cardDAO.findAllCardsByUserId(userId);
        boolean isDefault = existing.isEmpty() || request.isDefault();
        if (isDefault) {
            for (Card existingCard : existing) {
                if (existingCard.isDefaultCard()) {
                    existingCard.setDefaultCard(false);
                    cardDAO.updateCard(existingCard);
                }
            }
        }
        card.setDefaultCard(isDefault);
        card.setCardType(CardType.fromStripeBrand(paymentMethod.getCard().getBrand()));

        cardDAO.saveCard(card);

        AddCardResponse response = new AddCardResponse();
        response.setId(card.getCardId());
        response.setLast4(paymentMethod.getCard().getLast4());
        response.setBrand(paymentMethod.getCard().getBrand());
        response.setDefault(isDefault);
        return response;
    }

    @Transactional
    public AddCardRequest setDefaultCard(Long cardId, Long userId) {
        Card card = cardDAO.findCardById(cardId);
        if (card == null || !card.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Card not found or not owned by user");
        }

        List<Card> existing = cardDAO.findAllCardsByUserId(userId);
        for (Card existingCard : existing) {
            if (existingCard.isDefaultCard()) {
                existingCard.setDefaultCard(false);
                cardDAO.updateCard(existingCard);
            }
        }
        card.setDefaultCard(true);
        cardDAO.updateCard(card);

        return CardMapper.toAddCardRequest(card);
    }
}

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
     * Adds a new payment card for a user
     * Supports both secure tokenized approach (paymentMethodId) and raw card details
     */
    @Transactional
    public AddCardResponse addCard(AddCardRequest request, Long userId) throws StripeException {
        // 1. Fetch the user
        User user = userDAO.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        // 2. Create or retrieve Stripe Customer
        Customer customer = stripeService.getOrCreateCustomer(user.getEmail());
        if (user.getStripeCustomerId() == null || !user.getStripeCustomerId().equals(customer.getId())) {
            user.setStripeCustomerId(customer.getId());
            userDAO.updateUser(user);
        }

        // 3. Handle card processing with Stripe
        PaymentMethod paymentMethod;

        if (request.hasPaymentMethodId()) {
            // Preferred approach: use the payment method ID
            paymentMethod = stripeService.retrieveAndAttachPaymentMethod(
                    customer.getId(),
                    request.getPaymentMethodId()
            );
        } else {
            throw new IllegalArgumentException("paymentMethodId must be provided. Raw card details are not supported.");
        }

        // 4. Persist Card entity locally
        Card card = new Card();
        card.setUser(user);
        card.setStripePaymentMethodId(paymentMethod.getId());
        card.setHolderName(request.getHolderName());

        // Get card details from Stripe
        card.setExpireDate(YearMonth.of(
                Integer.parseInt(paymentMethod.getCard().getExpYear().toString()),
                Integer.parseInt(paymentMethod.getCard().getExpMonth().toString())
        ));

        // Store last 4 digits and brand for display purposes
        card.setCardNumber("xxxxxxxxxxxx" + paymentMethod.getCard().getLast4());

        // Determine default status: first card or explicit flag
        List<Card> existing = cardDAO.findAllCardsByUserId(userId);
        boolean isDefault = existing.isEmpty() || request.isDefault();
        card.setDefaultCard(isDefault);

        // If this is set as default, unset any previous default card
        if (isDefault && !existing.isEmpty()) {
            for (Card existingCard : existing) {
                if (existingCard.isDefaultCard()) {
                    existingCard.setDefaultCard(false);
                    cardDAO.updateCard(existingCard);
                }
            }
        }

        card.setCardType(CardType.fromStripeBrand(paymentMethod.getCard().getBrand()));

        // Save the new card
        cardDAO.saveCard(card);

        // 5. Build and return the response DTO
        AddCardResponse response = new AddCardResponse();
        response.setId(card.getCardId());
        response.setLast4(paymentMethod.getCard().getLast4());
        response.setBrand(paymentMethod.getCard().getBrand());
        response.setDefault(isDefault);
        return response;
    }

    @Transactional
    public AddCardRequest setDefaultCard(Long cardId, Long userId) {
        // Fetch the card
        Card card = cardDAO.findCardById(cardId);
        if (card == null) {
            throw new RuntimeException("Card not found with id: " + cardId);
        }

        // Verify ownership
        if (card.getUser().getId() != userId) {
            throw new RuntimeException("User is not the owner of the card");
        }

        // Find and update the previous default card
        Card previousDefaultCard = cardDAO.findDefaultCard();
        if (previousDefaultCard != null && !previousDefaultCard.getCardId().equals(cardId)) {
            previousDefaultCard.setDefaultCard(false);
            cardDAO.updateCard(previousDefaultCard);
        }

        // Set this card as default
        card.setDefaultCard(true);

        Card editedCard = cardDAO.updateCard(card);

        // Map to response object
        return CardMapper.toAddCardRequest(editedCard);
    }
}
package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.CardDAO;
import com.learnbridge.learn_bridge_back_end.dao.UserDAO;
import com.learnbridge.learn_bridge_back_end.dto.AddCardRequest;
import com.learnbridge.learn_bridge_back_end.dto.AddCardResponse;
import com.learnbridge.learn_bridge_back_end.entity.Card;
import com.learnbridge.learn_bridge_back_end.entity.User;
import com.learnbridge.learn_bridge_back_end.util.CardMapper;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentMethod;
import com.stripe.model.SetupIntent;
import com.stripe.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * Adds a new card for the logged-in user:
     * 1. Creates or retrieves a Stripe Customer
     * 2. Generates a SetupIntent for card vaulting
     * 3. Attaches the PaymentMethod to the Customer
     * 4. Updates the User entity with stripeCustomerId
     * 5. Persists Card entity with stripePaymentMethodId
     * 6. Returns a safe AddCardResponse DTO
     */
    @Transactional
    public AddCardResponse addCard(AddCardRequest request, Long userId) throws StripeException {
        // 1. Fetch user
        User user = userDAO.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        // 2. Create or retrieve Stripe Customer
        Customer customer = stripeService.getOrCreateCustomer(user.getEmail());
        // Persist stripeCustomerId on user
        user.setStripeCustomerId(customer.getId());
        userDAO.updateUser(user);

        // 3. Create SetupIntent (frontend will confirm via Stripe.js)
        SetupIntent setupIntent = stripeService.createSetupIntent(customer.getId());
        // Optionally return setupIntent.getClientSecret() if needed by frontend

        // 4. Attach PaymentMethod
        PaymentMethod paymentMethod = stripeService.attachPaymentMethod(
                customer.getId(),
                request.getPaymentMethodId()
        );

        // 5. Persist Card entity
        Card card = new Card();
        card.setUser(user);
        card.setStripePaymentMethodId(paymentMethod.getId());
        card.setHolderName(request.getHolderName());
        card.setExpireDate(request.getExpireDate());
        // Determine default: first card or explicit flag
        List<Card> existing = cardDAO.findAllCardsByUserId(userId);
        boolean isDefault = existing.isEmpty() || request.isDefault();
        card.setDefaultCard(isDefault);
        cardDAO.saveCard(card);

        // 6. Prepare response
        AddCardResponse response = new AddCardResponse();
        response.setId(card.getCardId());
        response.setLast4(paymentMethod.getCard().getLast4());
        response.setBrand(paymentMethod.getCard().getBrand());
        response.setDefault(isDefault);

        return response;
    }

    public AddCardRequest setDefaultCard(Long cardId, Long userId) {

        Card card = cardDAO.findCardById(cardId);
        if (card == null) {
            throw new RuntimeException("Card not found with id: " + cardId);
        }
        if(card.getUser().getId() != userId){
            throw new RuntimeException("User is not the owner of the card");
        }

        Card previousDefaultCard = cardDAO.findDefaultCard();
        if (previousDefaultCard != null) {
            card.setDefaultCard(false);
            cardDAO.updateCard(previousDefaultCard);
        }
        card.setDefaultCard(true);
        Card editedCard = cardDAO.updateCard(card);

        return CardMapper.toAddCardRequest(editedCard);
    }
}

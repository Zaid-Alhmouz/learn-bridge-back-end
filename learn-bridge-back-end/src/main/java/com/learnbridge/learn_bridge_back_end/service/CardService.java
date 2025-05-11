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
import com.stripe.param.PaymentMethodListParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        // 0) Lookup user
        User user = userDAO.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        // 1) Ensure Stripe customer exists
        Customer customer = stripeService.getOrCreateCustomer(user.getEmail());
        if (!customer.getId().equals(user.getStripeCustomerId())) {
            user.setStripeCustomerId(customer.getId());
            userDAO.updateUser(user);
        }

        // 2) List existing PMs for this customer
        List<PaymentMethod> existingPMs = stripeService.listCustomerPaymentMethods(
                customer.getId(),
                PaymentMethodListParams.Type.CARD
        );

        // 3) Derive candidate last-4 and expiry BEFORE any attach/create
        String candidateLast4;
        YearMonth candidateExpiry;

        if (request.getPaymentMethodId() != null && !request.getPaymentMethodId().isEmpty()) {
            // Token branch: fetch it first (without attaching) to read its details
            PaymentMethod incoming = stripeService.retrievePaymentMethod(request.getPaymentMethodId());
            candidateLast4     = incoming.getCard().getLast4();
            candidateExpiry    = YearMonth.of(
                    incoming.getCard().getExpYear().intValue(),
                    incoming.getCard().getExpMonth().intValue()
            );
        } else {
            // Raw-card branch: pull from the DTO
            String raw = request.getCardNumber().replaceAll("\\s+","");
            candidateLast4  = raw.substring(raw.length() - 4);
            candidateExpiry = request.getExpireDate();
        }

        // 4) Pre-check duplicates against existingPMs
        for (PaymentMethod pm : existingPMs) {
            String last4 = pm.getCard().getLast4();
            YearMonth exp = YearMonth.of(
                    pm.getCard().getExpYear().intValue(),
                    pm.getCard().getExpMonth().intValue()
            );
            if (last4.equals(candidateLast4) && exp.equals(candidateExpiry)) {
                throw new IllegalArgumentException(
                        "You’ve already added this card (ending in " +
                                last4 + ", exp " + exp + ")."
                );
            }
        }

        // 5) Create or attach now that we know it’s not a duplicate
        PaymentMethod pm;
        if (request.getPaymentMethodId() != null && !request.getPaymentMethodId().isEmpty()) {
            // attach the existing PM
            pm = stripeService.attachPaymentMethodToCustomer(
                    customer.getId(), request.getPaymentMethodId()
            );
        } else {
            // create & attach raw-card PM
            YearMonth exp = request.getExpireDate();
            pm = stripeService.createAndAttachCard(
                    customer.getId(),
                    request.getCardNumber(),
                    String.valueOf(exp.getMonthValue()),
                    String.valueOf(exp.getYear()),
                    request.getCvc()
            );
        }

        // 6) Make it default in Stripe
        stripeService.updateCustomerDefaultPaymentMethod(
                customer.getId(),
                pm.getId()
        );

        // 7) Persist in our DB
        String last4 = pm.getCard().getLast4();
        YearMonth expiry = YearMonth.of(
                pm.getCard().getExpYear().intValue(),
                pm.getCard().getExpMonth().intValue()
        );

        Card card = new Card();
        card.setUser(user);
        card.setStripePaymentMethodId(pm.getId());
        card.setHolderName(request.getHolderName());
        card.setExpireDate(expiry);
        card.setCardNumber("xxxxxxxxxxxx" + last4);
        card.setCardType(CardType.fromStripeBrand(pm.getCard().getBrand()));

        // Local default-flag logic
        List<Card> existingCards = cardDAO.findAllCardsByUserId(userId);
        boolean makeDefault = existingCards.isEmpty() || request.isDefault();
        if (makeDefault) {
            for (Card old : existingCards) {
                if (old.isDefaultCard()) {
                    old.setDefaultCard(false);
                    cardDAO.updateCard(old);
                }
            }
            card.setDefaultCard(true);
        } else {
            card.setDefaultCard(false);
        }

        cardDAO.saveCard(card);

        // 8) Build response
        AddCardResponse resp = new AddCardResponse();
        resp.setId(card.getCardId());
        resp.setLast4(last4);
        resp.setBrand(pm.getCard().getBrand());
        resp.setDefault(card.isDefaultCard());
        return resp;
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


    // check if user has card or not
    @Transactional(readOnly = true)
    public boolean userHasCard(Long userId) {
        List<Card> cards = cardDAO.findAllCardsByUserId(userId);
        return cards != null && !cards.isEmpty();
    }
}

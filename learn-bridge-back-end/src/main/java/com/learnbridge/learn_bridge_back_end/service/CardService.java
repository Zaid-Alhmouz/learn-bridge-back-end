package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.CardDAO;
import com.learnbridge.learn_bridge_back_end.dao.InstructorDAO;
import com.learnbridge.learn_bridge_back_end.dao.UserDAO;
import com.learnbridge.learn_bridge_back_end.dto.AddCardRequest;
import com.learnbridge.learn_bridge_back_end.dto.AddCardResponse;
import com.learnbridge.learn_bridge_back_end.dto.CardDTO;
import com.learnbridge.learn_bridge_back_end.entity.*;
import com.learnbridge.learn_bridge_back_end.util.CardMapper;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.PaymentMethodListParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.YearMonth;
import java.util.ArrayList;
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


    @Autowired
    private InstructorDAO instructorDAO;



    @Transactional
    public AddCardResponse addCard(AddCardRequest request, Long userId) throws StripeException {
        // 0) Lookup user
        User user = userDAO.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        // 1) Ensure Stripe Customer exists (for paying)
        Customer customer = stripeService.getOrCreateCustomer(user.getEmail());
        if (!customer.getId().equals(user.getStripeCustomerId())) {
            user.setStripeCustomerId(customer.getId());
            userDAO.updateUser(user);
        }

        // 1a) If INSTRUCTOR and no Connect account yet, create Express Connect account
        if (user.getUserRole() == UserRole.INSTRUCTOR) {
            Instructor instructor = instructorDAO.findInstructorById(user.getId());
            if (instructor.getStripeAccountId() == null) {
                AccountCreateParams acctParams = AccountCreateParams.builder()
                        .setType(AccountCreateParams.Type.EXPRESS)
                        .setCountry("US")  // or use a @Value-injected property
                        .setEmail(user.getEmail())
                        .setBusinessType(AccountCreateParams.BusinessType.INDIVIDUAL)
                        .setCapabilities(
                                AccountCreateParams.Capabilities.builder()
                                        .setCardPayments(
                                                AccountCreateParams.Capabilities.CardPayments.builder()
                                                        .setRequested(true)
                                                        .build()
                                        )
                                        .setTransfers(
                                                AccountCreateParams.Capabilities.Transfers.builder()
                                                        .setRequested(true)
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

                Account acct = Account.create(acctParams);
                instructor.setStripeAccountId(acct.getId());
                instructorDAO.updateInstructor(instructor);
            }
        }

        // 2) List existing PaymentMethods on the Customer
        List<PaymentMethod> existingPMs = stripeService.listCustomerPaymentMethods(
                customer.getId(),
                PaymentMethodListParams.Type.CARD
        );

        // 3) Compute candidate last4 + expiry BEFORE attach/create
        String candidateLast4;
        YearMonth candidateExpiry;
        if (StringUtils.hasText(request.getPaymentMethodId())) {
            PaymentMethod incoming = stripeService.retrievePaymentMethod(request.getPaymentMethodId());
            candidateLast4 = incoming.getCard().getLast4();
            candidateExpiry = YearMonth.of(
                    incoming.getCard().getExpYear().intValue(),
                    incoming.getCard().getExpMonth().intValue()
            );
        } else {
            String raw = request.getCardNumber().replaceAll("\\s+", "");
            candidateLast4 = raw.substring(raw.length() - 4);
            candidateExpiry = request.getExpireDate();
        }

        // 4) Prevent duplicate cards
        for (PaymentMethod pm : existingPMs) {
            YearMonth exp = YearMonth.of(
                    pm.getCard().getExpYear().intValue(),
                    pm.getCard().getExpMonth().intValue()
            );
            if (pm.getCard().getLast4().equals(candidateLast4) && exp.equals(candidateExpiry)) {
                throw new IllegalArgumentException(
                        "You’ve already added this card (ending in " + candidateLast4 +
                                ", exp " + candidateExpiry + ")."
                );
            }
        }

        // 5) Attach or create the new PaymentMethod
        PaymentMethod pm = StringUtils.hasText(request.getPaymentMethodId())
                ? stripeService.attachPaymentMethodToCustomer(customer.getId(), request.getPaymentMethodId())
                : stripeService.createAndAttachCard(
                customer.getId(),
                request.getCardNumber(),
                String.valueOf(request.getExpireDate().getMonthValue()),
                String.valueOf(request.getExpireDate().getYear()),
                request.getCvc()
        );

        // 6) Make it the default PaymentMethod on Stripe
        stripeService.updateCustomerDefaultPaymentMethod(customer.getId(), pm.getId());

        // 7) Persist card details in your DB
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

        List<Card> existingCards = cardDAO.findAllCardsByUserId(userId);
        boolean makeDefault = existingCards.isEmpty() || request.isDefault();
        if (makeDefault) {
            existingCards.stream()
                    .filter(Card::isDefaultCard)
                    .forEach(old -> {
                        old.setDefaultCard(false);
                        cardDAO.updateCard(old);
                    });
            card.setDefaultCard(true);
        } else {
            card.setDefaultCard(false);
        }
        cardDAO.saveCard(card);

        // 8) Build and return response
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

    public List<CardDTO> getAllCardsForUser(Long userId) {
        List<Card> cards = cardDAO.findAllCardsByUserId(userId);


        if (cards != null && !cards.isEmpty()) {

            return cards.stream().map(this::convertToCardDTO).collect(Collectors.toList());
        }

        else
            return new ArrayList<>();
    }

    @Transactional
    public CardDTO deleteCardByUserId(Long userId) {

        Card cardToBeDeleted = cardDAO.findCardByUserId(userId);
        if (cardToBeDeleted == null) {
            throw new IllegalArgumentException("Card not found or not owned by user");
        }
        Long cardId = cardToBeDeleted.getCardId();
        Card card = cardDAO.findCardById(cardId);
        if (card == null) {
            throw new IllegalArgumentException("Card not found or not owned by user");
        }
        CardDTO cardPreview = convertToCardDTO(card);
        cardDAO.deleteCard(cardId);
        return cardPreview;
    }

    private CardDTO convertToCardDTO(Card card) {
        return new CardDTO(card);
    }
}

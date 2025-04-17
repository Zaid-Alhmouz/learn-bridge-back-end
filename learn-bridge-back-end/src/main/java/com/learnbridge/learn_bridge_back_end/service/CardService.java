package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.CardDAO;
import com.learnbridge.learn_bridge_back_end.dao.UserDAO;
import com.learnbridge.learn_bridge_back_end.dto.AddCardRequest;
import com.learnbridge.learn_bridge_back_end.entity.Card;
import com.learnbridge.learn_bridge_back_end.entity.CardType;
import com.learnbridge.learn_bridge_back_end.entity.User;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class CardService {

    @Autowired
    private CardDAO cardDAO;

    @Autowired
    private UserDAO userDAO;


    public CardType determineCardType(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            throw new IllegalArgumentException("Card number is empty or null");
        }

        // remove any spaces or hyphens from the card number.
        cardNumber = cardNumber.replaceAll("[\\s-]", "");
        if (cardNumber.startsWith("4")) {
            return CardType.VISA;
        } else if (cardNumber.startsWith("5")) {
            return CardType.MASTERCARD;
        } else if (cardNumber.startsWith("34") || cardNumber.startsWith("37")) {
            return CardType.AMERICAN_EXPRESS;
        } else if (cardNumber.startsWith("6")) {
            return CardType.DISCOVER;
        }
        return CardType.UNKNOWN;
    }



    public Card addCard(AddCardRequest request, SecurityUser loggedUser) {
        Long userId = loggedUser.getUser().getId();
        User user = userDAO.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        Card card = new Card();
        card.setCardNumber(request.getCardNumber());
        card.setExpireDate(request.getExpireDate());
        card.setHolderName(request.getHolderName());

        // automatically determine the card type based on the card number.
        card.setCardType(determineCardType(request.getCardNumber()));

        List<Card> userCards = cardDAO.findAllCardsByUserId(userId);

        if (userCards.isEmpty() || userCards == null) {
            card.setDefaultCard(true);
        }
        else{
            card.setDefaultCard(false);
        }



        card.setUser(user);

        cardDAO.saveCard(card);
        return card;
    }
}

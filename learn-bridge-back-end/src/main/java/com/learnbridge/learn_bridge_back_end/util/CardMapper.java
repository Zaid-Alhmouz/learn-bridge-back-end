package com.learnbridge.learn_bridge_back_end.util;

import com.learnbridge.learn_bridge_back_end.dto.AddCardRequest;
import com.learnbridge.learn_bridge_back_end.entity.Card;

import java.util.List;
import java.util.stream.Collectors;

public class CardMapper {

    public static AddCardRequest toAddCardRequest(final Card card) {

        return new AddCardRequest(card);
    }

    public static List<AddCardRequest> toAddCardRequestList(final List<Card> cards) {

        return cards.stream().map(CardMapper::toAddCardRequest).collect(Collectors.toList());
    }
}

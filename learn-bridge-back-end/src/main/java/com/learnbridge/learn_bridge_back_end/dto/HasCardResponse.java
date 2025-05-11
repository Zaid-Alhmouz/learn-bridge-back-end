package com.learnbridge.learn_bridge_back_end.dto;

public class HasCardResponse {

    private boolean hasCard;

    public HasCardResponse() { }

    public HasCardResponse(boolean hasCard) {
        this.hasCard = hasCard;
    }

    public boolean isHasCard() {
        return hasCard;
    }

    public void setHasCard(boolean hasCard) {
        this.hasCard = hasCard;
    }
}

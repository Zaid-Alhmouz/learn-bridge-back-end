package com.learnbridge.learn_bridge_back_end.entity;

public enum CardType {

    VISA,
    MASTERCARD,
    AMERICAN_EXPRESS,
    DISCOVER,
    UNKNOWN;

    public static CardType fromStripeBrand(String brand) {
        return switch (brand.toLowerCase()) {
            case "visa" -> VISA;
            case "mastercard" -> MASTERCARD;
            case "amex" -> AMERICAN_EXPRESS;
            case "discover" -> DISCOVER;
            default -> UNKNOWN;
        };
    }
}


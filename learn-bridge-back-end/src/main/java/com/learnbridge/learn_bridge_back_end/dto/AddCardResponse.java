package com.learnbridge.learn_bridge_back_end.dto;

public class AddCardResponse {
    private Long id;           // Database ID of the Card entity
    private String last4;      // Last 4 digits of the card
    private String brand;      // Card brand (e.g., VISA, MASTERCARD)
    private boolean isDefault; // Whether this is the user's default card

    public AddCardResponse() {}

    public AddCardResponse(Long id, String last4, String brand, boolean isDefault) {
        this.id = id;
        this.last4 = last4;
        this.brand = brand;
        this.isDefault = isDefault;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getLast4() {
        return last4;
    }
    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }

    public boolean isDefault() {
        return isDefault;
    }
    public void setDefault(boolean aDefault) {
        this.isDefault = aDefault;
    }

    @Override
    public String toString() {
        return "AddCardResponse{" +
                "id=" + id +
                ", last4='" + last4 + '\'' +
                ", brand='" + brand + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}

package com.learnbridge.learn_bridge_back_end.dto;

import java.math.BigDecimal;

public class PersonalInfoDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String favouriteCategory;
    private String universityInfo;
    private String bio;
    private BigDecimal avgPrice;
    private byte[] personalImage;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFavouriteCategory() {
        return favouriteCategory;
    }

    public void setFavouriteCategory(String favouriteCategory) {
        this.favouriteCategory = favouriteCategory;
    }

    public String getUniversityInfo() {
        return universityInfo;
    }

    public void setUniversityInfo(String universityInfo) {
        this.universityInfo = universityInfo;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public BigDecimal getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPrice = avgPrice;
    }

    public byte[] getPersonalImage() {
        return personalImage;
    }

    public void setPersonalImage(byte[] personalImage) {
        this.personalImage = personalImage;
    }
}

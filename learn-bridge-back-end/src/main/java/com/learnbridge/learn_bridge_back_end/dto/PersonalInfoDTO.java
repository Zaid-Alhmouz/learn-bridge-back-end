package com.learnbridge.learn_bridge_back_end.dto;

import com.learnbridge.learn_bridge_back_end.entity.UserRole;

import java.math.BigDecimal;
import java.util.Arrays;

public class PersonalInfoDTO {

    private UserRole userRole;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String favouriteCategory;
    private String universityInfo;
    private String bio;
    private BigDecimal avgPrice;
    private String personalImage;


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

    public String getPersonalImage() {
        return personalImage;
    }

    public void setPersonalImage(String personalImage) {
        this.personalImage = personalImage;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "PersonalInfoDTO{" +
                "userRole=" + userRole +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", favouriteCategory='" + favouriteCategory + '\'' +
                ", universityInfo='" + universityInfo + '\'' +
                ", bio='" + bio + '\'' +
                ", avgPrice=" + avgPrice +
                ", personalImage='" + personalImage + '\'' +
                '}';
    }
}

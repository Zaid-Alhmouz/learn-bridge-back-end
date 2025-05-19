package com.learnbridge.learn_bridge_back_end.entity;

import jakarta.persistence.*;

import java.util.Arrays;

@Entity
@Table(name = "learner")
public class Learner {

    @Id
    @Column(name = "learner_id")
    private Long learnerId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "learner_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "favourite_category")
    private String favouriteCategory;

    @Lob
    @Column(name = "personal_image", columnDefinition = "MEDIUMBLOB")
    private byte[] personalImage;

    @Column(name = "stripe_account_id", length = 255, unique = true)
    private String stripeAccountId;

    public Long getLearnerId() {
        return learnerId;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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

    public String getFavouriteCategory() {
        return favouriteCategory;
    }

    public void setFavouriteCategory(String favouriteCategory) {
        this.favouriteCategory = favouriteCategory;
    }

    public byte[] getPersonalImage() {
        return personalImage;
    }

    public void setPersonalImage(byte[] personalImage) {
        this.personalImage = personalImage;
    }


    public String getStripeAccountId() {
        return stripeAccountId;
    }

    public void setStripeAccountId(String stripeAccountId) {
        this.stripeAccountId = stripeAccountId;
    }

    @Override
    public String toString() {
        return "Learner{" +
                "learnerId=" + learnerId +
                ", user=" + user +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", favouriteCategory='" + favouriteCategory + '\'' +
                ", personalImage=" + Arrays.toString(personalImage) +
                ", stripeAccountId='" + stripeAccountId + '\'' +
                '}';
    }
}

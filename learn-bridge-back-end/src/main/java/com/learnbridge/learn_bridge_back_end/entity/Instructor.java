package com.learnbridge.learn_bridge_back_end.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Arrays;

@Entity
@Table(name = "instructor")
public class Instructor {

    @Id
    @Column(name = "instructor_id")
    private Long instructorId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "instructor_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "bio")
    private String instructorBio;

    @Column(name = "avg_price")
    private BigDecimal avgPrice;

    @Column(name = "university_info")
    private String universityInfo;

    @Column(name = "instructor_image")
    private byte[] instructorImage;

    @Column(name = "favourite_category")
    private String favouriteCategory;

    @Column(name = "stripe_account_id", length = 255, unique = true)
    private String stripeAccountId;

    public Long getInstructorId() {
        return instructorId;
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

    public String getInstructorBio() {
        return instructorBio;
    }

    public void setInstructorBio(String instructorBio) {
        this.instructorBio = instructorBio;
    }

    public BigDecimal getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPrice = avgPrice;
    }

    public String getUniversityInfo() {
        return universityInfo;
    }

    public void setUniversityInfo(String universityInfo) {
        this.universityInfo = universityInfo;
    }

    public byte[] getInstructorImage() {
        return instructorImage;
    }

    public void setInstructorImage(byte[] instructorImage) {
        this.instructorImage = instructorImage;
    }

    public String getFavouriteCategory() {
        return favouriteCategory;
    }

    public void setFavouriteCategory(String favouriteCategory) {
        this.favouriteCategory = favouriteCategory;
    }


    public String getStripeAccountId() { return stripeAccountId; }
    public void setStripeAccountId(String stripeAccountId) {
        this.stripeAccountId = stripeAccountId;
    }


    @Override
    public String toString() {
        return "Instructor{" +
                "instructorId=" + instructorId +
                ", user=" + user +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", instructorBio='" + instructorBio + '\'' +
                ", avgPrice=" + avgPrice +
                ", universityInfo='" + universityInfo + '\'' +
                ", instructorImage=" + Arrays.toString(instructorImage) +
                ", favouriteCategory='" + favouriteCategory + '\'' +
                ", stripeAccountId='" + stripeAccountId + '\'' +
                '}';
    }
}

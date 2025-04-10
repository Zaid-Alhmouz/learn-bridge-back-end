package com.learnbridge.learn_bridge_back_end.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

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
                '}';
    }
}

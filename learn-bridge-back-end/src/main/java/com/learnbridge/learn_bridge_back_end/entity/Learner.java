package com.learnbridge.learn_bridge_back_end.entity;

import jakarta.persistence.*;

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

    @Override
    public String toString() {
        return "Learner{" +
                "learnerId=" + learnerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}

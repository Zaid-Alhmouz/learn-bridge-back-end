package com.learnbridge.learn_bridge_back_end.dto;

import com.learnbridge.learn_bridge_back_end.entity.Instructor;

import java.math.BigDecimal;

public class InstructorDTO {

    private Long instructorId;
    private String firstName;
    private String lastName;
    private String universityInfo;
    private String bio;
    private BigDecimal avgPrice;
    private String favouriteCategory;

    public InstructorDTO() {
    }

    public InstructorDTO(Instructor instructor) {
        this.instructorId = instructor.getInstructorId();
        this.firstName = instructor.getFirstName();
        this.lastName = instructor.getLastName();
        this.universityInfo = instructor.getUniversityInfo();
        this.bio = instructor.getInstructorBio();
        this.avgPrice = instructor.getAvgPrice();
        this.favouriteCategory = instructor.getFavouriteCategory();
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
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

    public String getFavouriteCategory() {
        return favouriteCategory;
    }

    public void setFavouriteCategory(String favouriteCategory) {
        this.favouriteCategory = favouriteCategory;
    }

    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPrice = avgPrice;
    }

    @Override
    public String toString() {
        return "InstructorDTO{" +
                "instructorId=" + instructorId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", universityInfo='" + universityInfo + '\'' +
                ", bio='" + bio + '\'' +
                ", avgPrice=" + avgPrice +
                ", favouriteCategory='" + favouriteCategory + '\'' +
                '}';
    }
}

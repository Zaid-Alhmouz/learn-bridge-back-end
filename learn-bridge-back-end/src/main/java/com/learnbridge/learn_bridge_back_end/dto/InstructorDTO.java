package com.learnbridge.learn_bridge_back_end.dto;

import com.learnbridge.learn_bridge_back_end.dao.SessionDAO;
import com.learnbridge.learn_bridge_back_end.entity.Instructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class InstructorDTO {

    private Long instructorId;
    private String firstName;
    private String lastName;
    private String universityInfo;
    private String bio;
    private BigDecimal avgPrice;
    private String favouriteCategory;
    private Integer numberOfSessions;
    private Integer numberOfReviews;
    private Double ratingAvg;
    private String personalImage;




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

    public InstructorDTO(
            Long instructorId,
            String firstName,
            String lastName,
            String universityInfo,
            String bio,
            BigDecimal avgPrice,
            String favouriteCategory,
            long numberOfSessions,
            long numberOfReviews,
            double ratingAvg
    ) {
        this.instructorId = instructorId;
        this.firstName      = firstName;
        this.lastName       = lastName;
        this.universityInfo = universityInfo;
        this.bio            = bio;
        this.avgPrice       = avgPrice;
        this.favouriteCategory = favouriteCategory;
        this.numberOfSessions  = (int) numberOfSessions;
        this.numberOfReviews   = (int) numberOfReviews;
        this.ratingAvg         = ratingAvg;
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

    public Integer getNumberOfSessions() {
        return numberOfSessions;
    }

    public void setNumberOfSessions(Integer numberOfSessions) {
        this.numberOfSessions = numberOfSessions;
    }

    public Integer getNumberOfReviews() {
        return numberOfReviews;
    }

    public void setNumberOfReviews(Integer numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

    public Double getRatingAvg() {
        return ratingAvg;
    }

    public void setRatingAvg(Double ratingAvg) {
        this.ratingAvg = ratingAvg;
    }

    public String getPersonalImage() {
        return personalImage;
    }

    public void setPersonalImage(String personalImage) {
        this.personalImage = personalImage;
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
                ", numberOfSessions=" + numberOfSessions +
                ", numberOfReviews=" + numberOfReviews +
                ", ratingAvg=" + ratingAvg +
                ", personalImage='" + personalImage + '\'' +
                '}';
    }
}

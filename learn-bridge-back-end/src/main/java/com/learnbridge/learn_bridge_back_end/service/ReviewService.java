package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.*;
import com.learnbridge.learn_bridge_back_end.dto.ReviewDTO;
import com.learnbridge.learn_bridge_back_end.entity.*;
import com.learnbridge.learn_bridge_back_end.util.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private RatingDAO ratingDAO;

    @Autowired
    private SessionDAO sessionDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private InstructorDAO instructorDAO;

    @Autowired
    private LearnerDAO learnerDAO;

    @Autowired
    private SessionParticipantsDAO sessionParticipantsDAO;


    // add review service
    public ReviewDTO addReview(ReviewDTO reviewDTO) {

        Rating ratingToBeAdded = new Rating();

        Session session = sessionDAO.findSessionById(reviewDTO.getSessionId());
        Instructor instructor = instructorDAO.findInstructorById(reviewDTO.getInstructorId());
        Learner learner = learnerDAO.findLearnerById(reviewDTO.getLearnerId());
        List<SessionParticipants> participants = sessionParticipantsDAO.findParticipantsBySession(session);

        boolean found = false;

        if (learner == null || session == null || instructor==null) {
            throw new RuntimeException("Learner or session or learner not found");
        }

        if (!(session.getInstructor().getId().equals(instructor.getInstructorId())))
        {
            throw new RuntimeException("Instructor does not belong to session");
        }

        for(SessionParticipants participant: participants) {
            if (participant.getSession().equals(session)) {
                found = true;
            }
        }
        if (!found) {
            throw new RuntimeException("Learner does not belong to session");
        }

        ratingToBeAdded.setSession(session);
        ratingToBeAdded.setInstructor(instructor);
        ratingToBeAdded.setLearner(learner);
        ratingToBeAdded.setDescription(reviewDTO.getDescription());
        ratingToBeAdded.setStars(reviewDTO.getStars());
        ratingToBeAdded.setReviewDate(reviewDTO.getReviewDate());

        return ReviewMapper.toDTO(ratingDAO.saveRating(ratingToBeAdded));
    }

    // learner can delete his review
    public ReviewDTO deleteReview(Long reviewId) {

        Rating ratingToBeDeleted = ratingDAO.findRatingByRatingId(reviewId);

        if (ratingToBeDeleted == null) {
            throw new RuntimeException("Review not found");
        }
        ratingDAO.deleteRating(reviewId);

        return ReviewMapper.toDTO(ratingToBeDeleted);
    }

    // get all reviews for instructor
    public List<ReviewDTO> getAllReviews(Long instructorId) {

        List<Rating> instructorRatings = ratingDAO.findRatingsByInstructorId(instructorId);

        if (instructorRatings == null) {
            return new ArrayList<>();
        }

        return ReviewMapper.toDTOList(instructorRatings);
    }
}

package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.*;
import com.learnbridge.learn_bridge_back_end.dto.ReviewDTO;
import com.learnbridge.learn_bridge_back_end.dto.ReviewSummaryDTO;
import com.learnbridge.learn_bridge_back_end.entity.*;
import com.learnbridge.learn_bridge_back_end.util.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private ChatDAO chatDAO;



    public ReviewDTO addReviewByChat(
            Long chatId,
            Long reviewerId,
            int stars,
            String description
    ) {
        Chat chat = chatDAO.findChatById(chatId);
        if (chat == null) throw new RuntimeException("Chat not found: " + chatId);
        Session session = chat.getSession();
        if (session == null) throw new RuntimeException("Session not found for chat: " + chatId);

        User reviewer = userDAO.findUserById(reviewerId);
        if (reviewer == null) throw new RuntimeException("Reviewer not found: " + reviewerId);

        UserRole role = reviewer.getUserRole();
        Rating rating = new Rating();
        rating.setSession(session);
        rating.setStars(stars);
        rating.setDescription(description);

        if (role == UserRole.LEARNER) {
            // learner reviews instructor
            Instructor instr = instructorDAO.findInstructorById(session.getInstructor().getId());
            if (instr == null) throw new RuntimeException("Instructor not in session " + session.getSessionId());
            rating.setInstructor(instr);
            Learner learner = learnerDAO.findLearnerById(reviewerId);
            if (learner == null) throw new RuntimeException("Learner not found: " + reviewerId);
            rating.setLearner(learner);
        } else if (role == UserRole.INSTRUCTOR) {
            // instructor reviews learner
            List<SessionParticipants> parts = sessionParticipantsDAO.findParticipantsBySession(session);
            if (parts.isEmpty()) throw new RuntimeException("No learner in session " + session.getSessionId());
            Learner learner = learnerDAO.findLearnerById(parts.get(0).getLearnerId());
            if (learner == null) throw new RuntimeException("Learner not found");
            rating.setLearner(learner);
            Instructor instr = instructorDAO.findInstructorById(reviewerId);
            if (instr == null) throw new RuntimeException("Instructor not found: " + reviewerId);
            rating.setInstructor(instr);
        } else {
            throw new RuntimeException("Role not allowed for review: " + role);
        }

        Rating saved = ratingDAO.saveRating(rating);
        return ReviewMapper.toDTO(saved);
    }


    public ReviewDTO deleteReviewByChat(Long chatId, Long reviewerId) {
        Chat chat = chatDAO.findChatById(chatId);
        if (chat == null) throw new RuntimeException("Chat not found: " + chatId);
        Session session = chat.getSession();
        if (session == null) throw new RuntimeException("Session not found for chat: " + chatId);

        // find rating by session and reviewer role
        UserRole role = userDAO.findUserById(reviewerId).getUserRole();
        Rating rating;
        if (role == UserRole.LEARNER) {
            rating = ratingDAO.findRatingBySessionAndLearnerId(session.getSessionId(), reviewerId);
        } else if (role == UserRole.INSTRUCTOR) {
            // need a DAO method to find by session and instructor
            rating = ratingDAO.findRatingBySessionAndInstructorId(session.getSessionId(), reviewerId);
        } else {
            throw new RuntimeException("Role not allowed for delete: " + role);
        }

        if (rating == null) throw new RuntimeException("Rating not found for session: " + session.getSessionId());
        ratingDAO.deleteRating(rating.getRatingId());
        return ReviewMapper.toDTO(rating);
    }

    // get all reviews for instructor
    public List<ReviewDTO> getAllReviews(Long instructorId) {

        List<Rating> instructorRatings = ratingDAO.findRatingsByInstructorId(instructorId);

        if (instructorRatings == null) {
            return new ArrayList<>();
        }

        return ReviewMapper.toDTOList(instructorRatings);
    }


    public List<ReviewSummaryDTO> getReviewsByInstructor(Long instructorId) {
        List<Rating> ratings = ratingDAO.findRatingsByInstructorId(instructorId);

        return ratings.stream()
                .map(r -> new ReviewSummaryDTO(
                        r.getLearner().getUser().getFirstName() + " " + r.getLearner().getUser().getLastName(),
                        r.getStars(),
                        r.getDescription()
                ))
                .collect(Collectors.toList());
    }
}

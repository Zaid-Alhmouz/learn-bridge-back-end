package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.*;
import com.learnbridge.learn_bridge_back_end.dto.AgreementRequestDTO;
import com.learnbridge.learn_bridge_back_end.dto.AgreementResponseDTO;
import com.learnbridge.learn_bridge_back_end.dto.AskForAgreementInfo;
import com.learnbridge.learn_bridge_back_end.entity.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgreementService {

    @Autowired
    private AgreementDAO agreementDAO;

    @Autowired
    private InstructorDAO instructorDAO;

    @Autowired
    private LearnerDAO learnerDAO;

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private NotificationService notificationsService;

    @Autowired
    private SessionDAO sessionDAO;

    @Autowired
    private RatingDAO ratingDAO;

    @Autowired
    private NotificationsDAO notificationsDAO;


    // create Agreement between learner and instructor via learner's post
    @Transactional
    public AgreementResponseDTO createAgreement(Long instructorId, Long learnerId, Long postId) {
        // find instructor, learner
        Instructor instructor = instructorDAO.findInstructorById(instructorId);
        Learner learner = learnerDAO.findLearnerById(learnerId);

        // find post
        PostId compositePostId = new PostId(learnerId, postId);
        Post post = postDAO.findPostById(compositePostId);

        if (!instructor.getFavouriteCategory().equalsIgnoreCase(post.getCategory())) {
            throw new IllegalArgumentException(
                    "Instructor's favourite category (“"
                            + instructor.getFavouriteCategory()
                            + "”) does not match post category (“"
                            + post.getCategory()
                            + "”)."
            );
        }

        // create and save a new agreement
        Agreement agreement = new Agreement();
        agreement.setInstructor(instructor);
        agreement.setLearner(learner);
        agreement.setPost(post);
        agreement.setPrice(post.getPrice());


        Agreement savedAgreement = agreementDAO.saveAgreement(agreement);

        // create notification for learner
        notificationsService.createAgreementRequestNotification(savedAgreement);

        // convertToResponseDTO
        return convertToResponseDTO(savedAgreement);
    }

    public List<AgreementResponseDTO> getAgreementByLearner(Long learnerId)
    {

        List<Agreement> agreement = agreementDAO.findAgreementByLearnerId(learnerId);

        return agreement.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }



    @Transactional
    public AgreementResponseDTO createLearnerInitiatedAgreement(Long learnerId, Long instructorId) {
        Learner learner     = learnerDAO.findLearnerById(learnerId);
        Instructor instructor = instructorDAO.findInstructorById(instructorId);

        Agreement ag = new Agreement();
        ag.setLearner(learner);
        ag.setInstructor(instructor);
        // no Post → store instructor’s avgPrice on the agreement itself (if you’ve added `price`)
        ag.setPrice(instructor.getAvgPrice());

        Agreement saved = agreementDAO.saveAgreement(ag);
        notificationsService.createLearnerRequestNotification(saved);
        return convertToResponseDTO(saved);
    }



    public AgreementResponseDTO getAgreementById(Long agreementId) {
        Agreement agreement = agreementDAO.findAgreementById(agreementId);
        if (agreement == null) {
            throw new RuntimeException("Agreement not found with id: " + agreementId);
        }
        return convertToResponseDTO(agreement);
    }

    private AgreementResponseDTO convertToResponseDTO(Agreement agreement) {
        AgreementResponseDTO dto = new AgreementResponseDTO();
        dto.setAgreementId(agreement.getAgreementId());
        dto.setInstructorName(
                agreement.getInstructor().getFirstName() + " " +
                        agreement.getInstructor().getLastName()
        );
        dto.setLearnerName(
                agreement.getLearner().getFirstName() + " " +
                        agreement.getLearner().getLastName()
        );

        if (agreement.getPost() != null) {
            dto.setPostSSubject(agreement.getPost().getSubject());
            dto.setPostStatus(agreement.getPost().getPostStatus().toString());
        } else {
            // learner-initiated: no post
            dto.setPostSSubject("N/A");
            dto.setPostStatus("N/A");
            // if you’ve added a price field on Agreement, you could also:
            // dto.setPrice( agreement.getPrice().toString() );
        }

        return dto;
    }


    @Transactional(readOnly = true)
    public AskForAgreementInfo getAskForAgreementInfo(Long notificationId) {
        Notifications notification = notificationsDAO.findNotificationById(notificationId);
        if (notification == null || notification.getAgreement() == null) {
            throw new EntityNotFoundException("Notification or its Agreement not found: " + notificationId);
        }
        Agreement ag = notification.getAgreement();

        Instructor instr = ag.getInstructor();
        Learner learner = ag.getLearner();
        Post post = ag.getPost(); // may be null if learner-initiated

        AskForAgreementInfo info = new AskForAgreementInfo();
        info.setAgreementId(ag.getAgreementId());
        info.setInstructorId(instr.getInstructorId());
        info.setLearnerId(learner.getLearnerId());
        info.setPostId(post != null ? post.getPostId() : null);

        // Basic subject/category/description
        info.setCategory(post != null ? post.getCategory() : null);
        info.setSubject(post != null ? post.getSubject() : null);
        info.setDescription(post != null ? post.getContent() : null);
        info.setPrice(post != null ? post.getPrice() : BigDecimal.ZERO);

        // Names & bio
        info.setLearnerName(
                learner.getFirstName() + " " + learner.getLastName()
        );
        info.setInstructorName(
                instr.getFirstName() + " " + instr.getLastName()
        );
        info.setInstructorBio(instr.getInstructorBio());

        // Sessions count (e.g., all finished sessions for this instructor)
        long sessionsNumber = sessionDAO
                .findFinishedSessionByInstructorId(instr.getInstructorId())
                .size();
        info.setSessionsNumber(sessionsNumber);

        // Ratings for instructor
        List<Rating> ratings = ratingDAO.findRatingsByInstructorId(instr.getInstructorId());
        info.setRatingsNumber((long) ratings.size());
        if (!ratings.isEmpty()) {
            BigDecimal avg = ratings.stream()
                    .map(r -> BigDecimal.valueOf(r.getStars()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(ratings.size()), 2, RoundingMode.HALF_UP);
            info.setAverageRating(avg);
        } else {
            info.setAverageRating(BigDecimal.ZERO);
        }

        return info;
    }

}


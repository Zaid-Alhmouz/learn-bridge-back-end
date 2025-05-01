package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.AgreementDAO;
import com.learnbridge.learn_bridge_back_end.dao.InstructorDAO;
import com.learnbridge.learn_bridge_back_end.dao.LearnerDAO;
import com.learnbridge.learn_bridge_back_end.dao.PostDAO;
import com.learnbridge.learn_bridge_back_end.dto.AgreementRequestDTO;
import com.learnbridge.learn_bridge_back_end.dto.AgreementResponseDTO;
import com.learnbridge.learn_bridge_back_end.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}


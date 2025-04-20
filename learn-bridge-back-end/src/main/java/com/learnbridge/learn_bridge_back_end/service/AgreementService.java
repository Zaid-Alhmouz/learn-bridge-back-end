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

    @Transactional
    public AgreementResponseDTO createAgreement(Long instructorId, Long learnerId, Long postId) {
        // find instructor, learner
        Instructor instructor = instructorDAO.findInstructorById(instructorId);
        Learner learner = learnerDAO.findLearnerById(learnerId);

        // find post
        PostId compositePostId = new PostId(learnerId, postId);
        Post post = postDAO.findPostById(compositePostId);


        // create and save a new agreement
        Agreement agreement = new Agreement();
        agreement.setInstructor(instructor);
        agreement.setLearner(learner);
        agreement.setPost(post);


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


    public AgreementResponseDTO getAgreementById(Long agreementId) {
        Agreement agreement = agreementDAO.findAgreementById(agreementId);
        if (agreement == null) {
            throw new RuntimeException("Agreement not found with id: " + agreementId);
        }
        return convertToResponseDTO(agreement);
    }

    private AgreementResponseDTO convertToResponseDTO(Agreement agreement) {
        AgreementResponseDTO responseDTO = new AgreementResponseDTO();
        responseDTO.setAgreementId(agreement.getAgreementId());
        responseDTO.setInstructorName(agreement.getInstructor().getFirstName() + " " + agreement.getInstructor().getLastName());
        responseDTO.setLearnerName(agreement.getLearner().getFirstName() + " " + agreement.getLearner().getLastName());
        responseDTO.setPostSSubject(agreement.getPost().getSubject());
        responseDTO.setPostStatus(agreement.getPost().getPostStatus().toString());
        return responseDTO;
    }
}


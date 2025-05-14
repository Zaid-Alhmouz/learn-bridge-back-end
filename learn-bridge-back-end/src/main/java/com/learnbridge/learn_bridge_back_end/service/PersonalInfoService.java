package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.InstructorDAO;
import com.learnbridge.learn_bridge_back_end.dao.LearnerDAO;
import com.learnbridge.learn_bridge_back_end.dao.UserDAO;
import com.learnbridge.learn_bridge_back_end.dto.PersonalInfoDTO;
import com.learnbridge.learn_bridge_back_end.entity.Instructor;
import com.learnbridge.learn_bridge_back_end.entity.Learner;
import com.learnbridge.learn_bridge_back_end.entity.User;
import com.learnbridge.learn_bridge_back_end.entity.UserRole;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class PersonalInfoService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private InstructorDAO instructorDAO;

    @Autowired
    private LearnerDAO learnerDAO;

    @Autowired
    private PasswordEncoder passwordEncoder; // make sure this is properly configured

    public void editPersonalInfo(PersonalInfoDTO personalInfoDTO, SecurityUser loggedUser) {
        // Retrieve the current user from the database
        Long userId = loggedUser.getUser().getId();
        User existingUser = userDAO.findUserById(userId);
        if (existingUser == null) {
            throw new RuntimeException("User not found for id: " + userId);
        }

        // Optionally, check if email changed and if new email is already taken
        if (!existingUser.getEmail().equals(personalInfoDTO.getEmail())) {
            User userWithNewEmail = userDAO.findUserByEmail(personalInfoDTO.getEmail());
            if (userWithNewEmail != null) {
                throw new RuntimeException("The email is already in use by another account.");
            }
            existingUser.setEmail(personalInfoDTO.getEmail());
        }

        // Update other user fields with new values from the DTO.
        existingUser.setFirstName(personalInfoDTO.getFirstName());
        existingUser.setLastName(personalInfoDTO.getLastName());
        // Update password only if a new one is provided (and encode it)
        if (personalInfoDTO.getPassword() != null && !personalInfoDTO.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(personalInfoDTO.getPassword());
            existingUser.setPassword(encodedPassword);
        }

        // Save the updated user info
        userDAO.updateUser(existingUser);

        // Depending on the user's role, update Learner or Instructor details:
        if (existingUser.getUserRole() == UserRole.LEARNER) {
            Learner existingLearner = learnerDAO.findLearnerById(userId);
            if (existingLearner == null) {
                throw new RuntimeException("Learner not found for user id: " + userId);
            }
            existingLearner.setFirstName(personalInfoDTO.getFirstName());
            existingLearner.setLastName(personalInfoDTO.getLastName());
            existingLearner.setFavouriteCategory(personalInfoDTO.getFavouriteCategory());
            existingLearner.setPersonalImage(personalInfoDTO.getPersonalImage());
            // Update other learner-specific fields, if any
            learnerDAO.updateLearner(existingLearner);
        } else if (existingUser.getUserRole() == UserRole.INSTRUCTOR) {
            Instructor existingInstructor = instructorDAO.findInstructorById(userId);
            if (existingInstructor == null) {
                throw new RuntimeException("Instructor not found for user id: " + userId);
            }
            existingInstructor.setFirstName(personalInfoDTO.getFirstName());
            existingInstructor.setLastName(personalInfoDTO.getLastName());
            existingInstructor.setFavouriteCategory(personalInfoDTO.getFavouriteCategory());
            existingInstructor.setUniversityInfo(personalInfoDTO.getUniversityInfo());
            existingInstructor.setInstructorBio(personalInfoDTO.getBio());
            existingInstructor.setAvgPrice(personalInfoDTO.getAvgPrice());
            existingInstructor.setInstructorImage(personalInfoDTO.getPersonalImage());
            instructorDAO.updateInstructor(existingInstructor);
        }
    }

    public PersonalInfoDTO getPersonalInfo(Long userId) {
        User user = userDAO.findUserById(userId);
        PersonalInfoDTO personalInfoDTO = new PersonalInfoDTO();
        if (user == null) {
            throw new RuntimeException("User not found for id: " + userId);
        }

        if (user.getUserRole() == UserRole.LEARNER) {
            Learner learner = learnerDAO.findLearnerById(userId);
            if (learner == null) {
                throw new RuntimeException("Learner not found for user id: " + userId);
            }

            personalInfoDTO.setFirstName(learner.getFirstName());
            personalInfoDTO.setLastName(learner.getLastName());
            personalInfoDTO.setPersonalImage(learner.getPersonalImage());
            personalInfoDTO.setFavouriteCategory(learner.getFavouriteCategory());
            personalInfoDTO.setEmail(user.getEmail());
            personalInfoDTO.setUniversityInfo(" ");
            personalInfoDTO.setBio(" ");
            personalInfoDTO.setAvgPrice(null);
            personalInfoDTO.setUserRole(user.getUserRole());
        }
        else if (user.getUserRole() == UserRole.INSTRUCTOR) {
            Instructor instructor = instructorDAO.findInstructorById(userId);
            if (instructor == null) {
                throw new RuntimeException("Instructor not found for user id: " + userId);
            }
            personalInfoDTO.setFirstName(instructor.getFirstName());
            personalInfoDTO.setLastName(instructor.getLastName());
            personalInfoDTO.setFavouriteCategory(instructor.getFavouriteCategory());
            personalInfoDTO.setUniversityInfo(instructor.getUniversityInfo());
            personalInfoDTO.setBio(instructor.getInstructorBio());
            personalInfoDTO.setAvgPrice(instructor.getAvgPrice());
            personalInfoDTO.setUserRole(user.getUserRole());
            personalInfoDTO.setPersonalImage(instructor.getInstructorImage());
            personalInfoDTO.setEmail(user.getEmail());
        }

        return personalInfoDTO;
    }
}
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
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PersonalInfoService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LearnerDAO learnerDAO;

    @Autowired
    private InstructorDAO instructorDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Fetches all personal info fields; converts stored byte[] → Base64 data-URI.
     */
    public PersonalInfoDTO getPersonalInfo(Long userId) {
        User user = userDAO.findUserById(userId);
        if (user == null) throw new RuntimeException("User not found: " + userId);

        PersonalInfoDTO dto = new PersonalInfoDTO();
        dto.setUserRole(user.getUserRole());
        dto.setEmail(user.getEmail());
        // Password not sent back
        dto.setPassword(null);

        if (user.getUserRole() == UserRole.LEARNER) {
            Learner learner = learnerDAO.findLearnerById(userId);
            if (learner == null) throw new RuntimeException("Learner not found: " + userId);
            dto.setFirstName(learner.getFirstName());
            dto.setLastName(learner.getLastName());
            dto.setFavouriteCategory(learner.getFavouriteCategory());
            dto.setUniversityInfo(null);
            dto.setBio(null);
            dto.setAvgPrice(null);

            byte[] img = learner.getPersonalImage();
            dto.setPersonalImage(img != null && img.length > 0
                    ? "data:image/jpeg;base64," + Base64.encodeBase64String(img)
                    : null
            );
        }
        else if (user.getUserRole() == UserRole.INSTRUCTOR) {
            Instructor inst = instructorDAO.findInstructorById(userId);
            if (inst == null) throw new RuntimeException("Instructor not found: " + userId);
            dto.setFirstName(inst.getFirstName());
            dto.setLastName(inst.getLastName());
            dto.setFavouriteCategory(inst.getFavouriteCategory());
            dto.setUniversityInfo(inst.getUniversityInfo());
            dto.setBio(inst.getInstructorBio());
            dto.setAvgPrice(inst.getAvgPrice());

            byte[] img = inst.getInstructorImage();
            dto.setPersonalImage(img != null && img.length > 0
                    ? "data:image/jpeg;base64," + Base64.encodeBase64String(img)
                    : null
            );
        }

        return dto;
    }

    /**
     * Stores an uploaded profile image (MultipartFile → byte[]) and returns
     * a Base64 data-URI for immediate front-end preview.
     */
    public String storeProfileImage(Long userId, MultipartFile file) {
        User user = userDAO.findUserById(userId);
        if (user == null) throw new RuntimeException("User not found: " + userId);

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read image file", e);
        }

        String dataUri = "data:" + file.getContentType() + ";base64,"
                + Base64.encodeBase64String(bytes);

        if (user.getUserRole() == UserRole.LEARNER) {
            Learner learner = learnerDAO.findLearnerById(userId);
            if (learner == null) throw new RuntimeException("Learner not found: " + userId);
            learner.setPersonalImage(bytes);
            learnerDAO.updateLearner(learner);
        } else {
            Instructor inst = instructorDAO.findInstructorById(userId);
            if (inst == null) throw new RuntimeException("Instructor not found: " + userId);
            inst.setInstructorImage(bytes);
            instructorDAO.updateInstructor(inst);
        }

        return dataUri;
    }


    public void editPersonalInfo(PersonalInfoDTO dto, SecurityUser loggedUser) {
        Long userId = loggedUser.getUser().getId();
        User user = userDAO.findUserById(userId);
        if (user == null) throw new RuntimeException("User not found: " + userId);

        // Email uniqueness check
        if (!user.getEmail().equals(dto.getEmail())) {
            User other = userDAO.findUserByEmail(dto.getEmail());
            if (other != null) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(dto.getEmail());
        }

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        userDAO.updateUser(user);

        if (user.getUserRole() == UserRole.LEARNER) {
            Learner learner = learnerDAO.findLearnerById(userId);
            learner.setFirstName(dto.getFirstName());
            learner.setLastName(dto.getLastName());
            learner.setFavouriteCategory(dto.getFavouriteCategory());
            learnerDAO.updateLearner(learner);
        } else {
            Instructor inst = instructorDAO.findInstructorById(userId);
            inst.setFirstName(dto.getFirstName());
            inst.setLastName(dto.getLastName());
            inst.setFavouriteCategory(dto.getFavouriteCategory());
            inst.setUniversityInfo(dto.getUniversityInfo());
            inst.setInstructorBio(dto.getBio());
            inst.setAvgPrice(dto.getAvgPrice());
            instructorDAO.updateInstructor(inst);
        }
    }
}

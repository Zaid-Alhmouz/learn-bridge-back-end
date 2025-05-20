package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.InstructorDAO;
import com.learnbridge.learn_bridge_back_end.dao.LearnerDAO;
import com.learnbridge.learn_bridge_back_end.dto.InstructorDTO;
import com.learnbridge.learn_bridge_back_end.entity.Instructor;
import com.learnbridge.learn_bridge_back_end.repository.InstructorStatsRepository;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class FindInstructorService {

    @Autowired
    private LearnerDAO learnerDAO;

    @Autowired
    private InstructorStatsRepository repo;

    @Autowired
    private InstructorDAO instructorDAO;

    /**
     * Retrieve instructors for the learner's favorite category,
     * then fetch and Base64-encode each instructor's image.
     */
    public List<InstructorDTO> findFavouriteInstructors(SecurityUser user) {
        String category = learnerDAO
                .findLearnerById(user.getUser().getId())
                .getFavouriteCategory();

        // get stats-only DTOs
        List<InstructorDTO> instructors = repo.findStatsByCategory(category);

        // for each, load raw entity, encode, and set personalImage
        for (InstructorDTO dto : instructors) {
            Instructor entity = instructorDAO.findInstructorById(dto.getInstructorId());
            byte[] imageBytes = entity.getInstructorImage();

            if (imageBytes != null && imageBytes.length > 0) {
                String base64 = Base64.getEncoder().encodeToString(imageBytes);
                dto.setPersonalImage("data:image/jpeg;base64," + base64);
            }
        }
        return instructors;
    }

    /**
     * Retrieve instructors for the selected category,
     * then fetch and Base64-encode each instructor's image.
     */
    public List<InstructorDTO> findSelectedCategoryInstructors(String selectedCategory) {
        // get stats-only DTOs
        List<InstructorDTO> instructors = repo.findStatsByCategory(selectedCategory);

        // for each, load raw entity, encode, and set personalImage
        for (InstructorDTO dto : instructors) {
            Instructor entity = instructorDAO.findInstructorById(dto.getInstructorId());
            byte[] imageBytes = entity.getInstructorImage();

            if (imageBytes != null && imageBytes.length > 0) {
                String base64 = Base64.getEncoder().encodeToString(imageBytes);
                dto.setPersonalImage("data:image/jpeg;base64," + base64);
            }
        }
        return instructors;
    }

    /**
     * View single instructor profile (stats-only).
     * If you also need the image here, repeat the Base64-encode step:
     */
    public InstructorDTO viewProfile(Long instructorId) {
        InstructorDTO dto = repo.findStatsById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found: " + instructorId));


        Instructor entity = instructorDAO.findInstructorById(instructorId);
        byte[] imageBytes = entity.getInstructorImage();
        if (imageBytes != null && imageBytes.length > 0) {
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            dto.setPersonalImage("data:image/jpeg;base64," + base64);
        }

        return dto;
    }
}

package com.learnbridge.learn_bridge_back_end.service;


import com.learnbridge.learn_bridge_back_end.dao.*;
import com.learnbridge.learn_bridge_back_end.dto.InstructorDTO;
import com.learnbridge.learn_bridge_back_end.dto.ReviewDTO;
import com.learnbridge.learn_bridge_back_end.entity.Instructor;
import com.learnbridge.learn_bridge_back_end.entity.Learner;
import com.learnbridge.learn_bridge_back_end.entity.Rating;
import com.learnbridge.learn_bridge_back_end.entity.Session;
import com.learnbridge.learn_bridge_back_end.repository.InstructorStatsRepository;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.util.InstructorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.learnbridge.learn_bridge_back_end.util.InstructorMapper.toDTOList;

@Service
public class FindInstructorService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private InstructorDAO instructorDAO;

    @Autowired
    private LearnerDAO learnerDAO;

    @Autowired
    private SessionDAO sessionDAO;

    @Autowired
    private RatingDAO ratingDAO;

    @Autowired
    private InstructorStatsRepository repo;



    // retrieve instructors for learners based on their favourite category
    public List<InstructorDTO> findFavouriteInstructors(SecurityUser user) {
        // get learner’s category…
        String category = learnerDAO.findLearnerById(user.getUser().getId())
                .getFavouriteCategory();
        return repo.findStatsByCategory(category);
    }

    // retrieve instructors for learners based on the selected category
    public List<InstructorDTO> findSelectedCategoryInstructors(String selectedCategory) {
        return repo.findStatsByCategory(selectedCategory);
    }

    // retrieve the selected instructor profile information
    public InstructorDTO viewProfile(Long instructorId) {
        return repo.findStatsById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found: " + instructorId));
    }



}

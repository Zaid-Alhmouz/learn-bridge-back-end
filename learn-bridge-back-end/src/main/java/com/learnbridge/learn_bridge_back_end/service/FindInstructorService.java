package com.learnbridge.learn_bridge_back_end.service;


import com.learnbridge.learn_bridge_back_end.dao.InstructorDAO;
import com.learnbridge.learn_bridge_back_end.dao.LearnerDAO;
import com.learnbridge.learn_bridge_back_end.dao.UserDAO;
import com.learnbridge.learn_bridge_back_end.dto.InstructorDTO;
import com.learnbridge.learn_bridge_back_end.entity.Instructor;
import com.learnbridge.learn_bridge_back_end.entity.Learner;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.util.InstructorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindInstructorService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private InstructorDAO instructorDAO;

    @Autowired
    private LearnerDAO learnerDAO;


    public List<InstructorDTO> findAllInstructorsWithFavouriteLearnerCategory(SecurityUser loggedUser) {

        Long userId = loggedUser.getUser().getId();
        Learner learner = learnerDAO.findLearnerById(userId);

        if (learner == null) {
            throw new RuntimeException("learner not found");
        }

        String learnerFavouriteCategory = learner.getFavouriteCategory();
        if (learnerFavouriteCategory == null) {

        }

        List<Instructor> instructors = instructorDAO.findAllInstructorsByFavouriteCategory(learnerFavouriteCategory);

        return InstructorMapper.toDTOList(instructors);
    }
}

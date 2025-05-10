package com.learnbridge.learn_bridge_back_end.controller;


import com.learnbridge.learn_bridge_back_end.dao.AdminDAO;
import com.learnbridge.learn_bridge_back_end.dao.InstructorDAO;
import com.learnbridge.learn_bridge_back_end.dao.LearnerDAO;
import com.learnbridge.learn_bridge_back_end.dao.UserDAO;
import com.learnbridge.learn_bridge_back_end.dto.UserRegistrationRequest;
import com.learnbridge.learn_bridge_back_end.entity.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private InstructorDAO instructorDAO;

    @Autowired
    private LearnerDAO learnerDAO;

    @Autowired
    private AdminDAO adminDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request)
    {
        // check if email exists
        if(userDAO.findUserByEmail(request.getEmail()) != null)
        {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Email already exists");

            return ResponseEntity.badRequest().body(error);
        }

        // create new user
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAccountStatus(AccountStatus.ACTIVE);



        // validate role and handle it
        UserRole role;
        try{
            role = UserRole.valueOf(request.getRole());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid role"));
        }
        user.setUserRole(role);


        // create Learner/Instructor profile
        if(role == UserRole.INSTRUCTOR)
        {
            // validate Instructor fields
            if(request.getBio() == null || request.getUniversityInfo() == null || request.getAvgPrice() == null || request.getFavouriteCategory() == null)
            {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid Instructor fields, fields required"));
            }

            Instructor instructor = new Instructor();
            instructor.setUser(user);
            instructor.setFirstName(request.getFirstName());
            instructor.setLastName(request.getLastName());
            instructor.setInstructorBio(request.getBio());
            instructor.setUniversityInfo(request.getUniversityInfo());
            instructor.setAvgPrice(request.getAvgPrice());
            instructor.setFavouriteCategory(request.getFavouriteCategory());

            // save instructor
            instructorDAO.saveInstructor(instructor);
        }

        else if(role == UserRole.LEARNER)
        {
            Learner learner = new Learner();
            learner.setUser(user);
            learner.setFirstName(request.getFirstName());
            learner.setLastName(request.getLastName());
            learner.setFavouriteCategory(request.getFavouriteCategory());

            // save learner
            learnerDAO.saveLearner(learner);
        }
        else if(role == UserRole.ADMIN)
        {
            throw new RuntimeException("Unauthorized");
        }
        // save user
        userDAO.saveUser(user);
        return ResponseEntity.ok().body(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/admin/create-admin")
//    @PreAuthorize("hasRole('ADMIN')") Remove this to create admins peacefully...
    public ResponseEntity<?> createAdmin(@Valid @RequestBody UserRegistrationRequest request) {
        if (userDAO.findUserByEmail(request.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User admin = new User();
        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setUserRole(UserRole.ADMIN);
        admin.setAccountStatus(AccountStatus.ACTIVE);

        Admin registeredAdmin = new Admin();
        registeredAdmin.setUser(admin);
        registeredAdmin.setFirstName(request.getFirstName());
        registeredAdmin.setLastName(request.getLastName());


        userDAO.saveUser(admin);
        adminDAO.saveAdmin(registeredAdmin);

        return ResponseEntity.ok("Admin created successfully");
    }
}

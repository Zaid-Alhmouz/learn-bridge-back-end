package com.learnbridge.learn_bridge_back_end.controller;


import com.learnbridge.learn_bridge_back_end.dao.UserDAO;
import com.learnbridge.learn_bridge_back_end.dto.UserRegistrationRequest;
import com.learnbridge.learn_bridge_back_end.entity.User;
import com.learnbridge.learn_bridge_back_end.entity.UserRole;
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
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getRole() != null) {
            try {
                user.setUserRole(UserRole.valueOf(request.getRole()));
            } catch (IllegalArgumentException e) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Invalid role specified");
                return ResponseEntity.badRequest().body(error);
            }
        } else {
            user.setUserRole(UserRole.LEARNER);
        }


        userDAO.saveUser(user);
        return ResponseEntity.ok().body(Map.of("message", "User registered successfully")); // Return JSON
    }

    @PostMapping("/admin/create-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody UserRegistrationRequest request) {
        if (userDAO.findUserByEmail(request.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User admin = new User();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setUserRole(UserRole.ADMIN);

        userDAO.saveUser(admin);
        return ResponseEntity.ok("Admin created successfully");
    }
}

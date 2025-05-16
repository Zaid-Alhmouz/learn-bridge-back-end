package com.learnbridge.learn_bridge_back_end.controller;

import com.learnbridge.learn_bridge_back_end.dao.UserDAO;
import com.learnbridge.learn_bridge_back_end.dto.UserDTO;
import com.learnbridge.learn_bridge_back_end.entity.AccountStatus;
import com.learnbridge.learn_bridge_back_end.entity.User;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.UserService;
import com.learnbridge.learn_bridge_back_end.util.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserDAO userDAO;

    @Autowired
    private UserService userService;

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        User user = securityUser.getUser();

        // Explicitly structure the response
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("email", user.getEmail());
        response.put("role", user.getUserRole().name()); // explicit role mapping
        response.put("firstName", user.getFirstName());

        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/blocked-users")
    public ResponseEntity<List<UserDTO>> getAllBlockedUsers() {

        List<UserDTO> blockedUsers = userService.getBlockedUsers();
        return ResponseEntity.ok(blockedUsers);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/unblock-user/{userId}")
    public ResponseEntity<UserDTO> unblockUser(@PathVariable Long userId) {

        UserDTO userDTO = userService.unblockUser(userId);
        return ResponseEntity.ok(userDTO);
    }

}

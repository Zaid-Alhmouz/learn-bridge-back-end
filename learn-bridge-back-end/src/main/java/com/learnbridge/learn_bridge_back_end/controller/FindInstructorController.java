package com.learnbridge.learn_bridge_back_end.controller;

import com.learnbridge.learn_bridge_back_end.dto.InstructorDTO;
import com.learnbridge.learn_bridge_back_end.entity.Instructor;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.FindInstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors")
@CrossOrigin(origins = "http://localhost:4200")
public class FindInstructorController {

    @Autowired
    private FindInstructorService findInstructorService;

    @GetMapping("/find-favourite")
    public ResponseEntity<List<InstructorDTO>> findFavourite(@AuthenticationPrincipal SecurityUser loggedUser) {
        try {
            List<InstructorDTO> foundInstructors = findInstructorService.findAllInstructorsWithFavouriteLearnerCategory(loggedUser);
            return new ResponseEntity<>(foundInstructors, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

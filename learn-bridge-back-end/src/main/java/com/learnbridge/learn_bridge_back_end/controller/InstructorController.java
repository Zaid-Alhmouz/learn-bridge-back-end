package com.learnbridge.learn_bridge_back_end.controller;

import com.learnbridge.learn_bridge_back_end.dto.InstructorDTO;
import com.learnbridge.learn_bridge_back_end.dto.ReviewSummaryDTO;
import com.learnbridge.learn_bridge_back_end.repository.InstructorStatsRepository;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.FindInstructorService;
import com.learnbridge.learn_bridge_back_end.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors")
@CrossOrigin(origins = "http://localhost:4200")
public class InstructorController {

    @Autowired
    private FindInstructorService findInstructorService;

    @Autowired
    private InstructorStatsRepository statsRepo;

    @Autowired
    private ReviewService reviewService;


    // to retrieve instructors based on learner's favourite category
    @GetMapping("/find-favourite")
    public ResponseEntity<List<InstructorDTO>> findFavouriteInstructors(@AuthenticationPrincipal SecurityUser loggedUser) {
        try {
            List<InstructorDTO> foundInstructors = findInstructorService.findFavouriteInstructors(loggedUser);
            return new ResponseEntity<>(foundInstructors, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // to retrieve instructors based on selected category
    @GetMapping("/{selectedCategory}")
    public ResponseEntity<List<InstructorDTO>> findSelectedCategoryInstructors(@PathVariable String selectedCategory) {
        try {
            List<InstructorDTO> foundInstructors = findInstructorService.findSelectedCategoryInstructors(selectedCategory);
            return new ResponseEntity<>(foundInstructors, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/view-profile/{instructorId}")
    public ResponseEntity<InstructorDTO> viewProfile(@PathVariable Long instructorId) {
        try {
            InstructorDTO dto = findInstructorService.viewProfile(instructorId);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // retrieve instructor profile info
    @GetMapping("/profile/{instructorId}")
    public ResponseEntity<InstructorDTO> getInstructorProfile(@PathVariable Long instructorId) {
        return statsRepo.findStatsById(instructorId)
                .map(dto -> ResponseEntity.ok(dto))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/view-profile/{instructorId}/reviews")
    public ResponseEntity<List<ReviewSummaryDTO>> getInstructorReviews(
            @PathVariable("instructorId") Long instructorId) {

        List<ReviewSummaryDTO> reviews = reviewService.getReviewsByInstructor(instructorId);
        return ResponseEntity.ok(reviews);
    }

}

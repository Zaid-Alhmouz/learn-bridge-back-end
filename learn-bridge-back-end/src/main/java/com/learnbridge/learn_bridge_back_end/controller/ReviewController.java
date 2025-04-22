package com.learnbridge.learn_bridge_back_end.controller;

import com.learnbridge.learn_bridge_back_end.dto.ReviewDTO;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/review")
@CrossOrigin(origins = "http://localhost:4200")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PreAuthorize("hasRole('LEARNER')")
    @PostMapping("/add-review/{sessionId}/{instructorId}")
    public ResponseEntity<ReviewDTO> addReview(@PathVariable Long sessionId, @PathVariable Long instructorId, @RequestBody ReviewDTO reviewDTO, @AuthenticationPrincipal SecurityUser loggedUser) {

        ReviewDTO reviewToBeCreated = new ReviewDTO();
        reviewToBeCreated.setSessionId(sessionId);
        reviewToBeCreated.setInstructorId(instructorId);
        reviewToBeCreated.setLearnerId(loggedUser.getUser().getId());
        reviewToBeCreated.setReviewDate(LocalDate.now());
        reviewToBeCreated.setDescription(reviewDTO.getDescription());
        reviewToBeCreated.setStars(reviewDTO.getStars());

        ReviewDTO addedReview = reviewService.addReview(reviewToBeCreated);

        if (addedReview == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(addedReview, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('LEARNER')")
    @DeleteMapping("/delete-review/{ratingId}")
    public ResponseEntity<ReviewDTO> deleteReview(@PathVariable Long ratingId) {
        ReviewDTO reviewToBeDeleted = reviewService.deleteReview(ratingId);
        if (reviewToBeDeleted == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(reviewToBeDeleted, HttpStatus.OK);
    }

    // for instructor when he wants to retrieve his reviews by learners
    @GetMapping("/instructor/all-reviews")
    public ResponseEntity<List<ReviewDTO>> getAllReviews(@AuthenticationPrincipal SecurityUser loggedUser) {

        Long instructorId = loggedUser.getUser().getId();
        List<ReviewDTO> reviews = reviewService.getAllReviews(instructorId);
        if (reviews == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/instructor/all-reviews/{instructorId}")
    public ResponseEntity<ReviewDTO> getReview(@PathVariable Long instructorId) {

        List<ReviewDTO> reviews = reviewService.getAllReviews(instructorId);
        if (reviews == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(reviews.get(0), HttpStatus.OK);
    }

}

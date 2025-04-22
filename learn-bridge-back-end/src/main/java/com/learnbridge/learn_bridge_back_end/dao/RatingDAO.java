package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Rating;

import java.util.List;

public interface RatingDAO {

    Rating saveRating(Rating rating);
    Rating updateRating(Rating rating);
    void deleteRating(Long ratingId);
    List<Rating> findAllRatings();
    Rating findRatingBySessionAndLearnerId(Long sessionId, Long learnerId); // To be revised
    List<Rating> findRatingsByInstructorId(Long instructorId);
    void deleteRatingBySessionAndLearnerId(Long sessionId, Long learnerId);
    Rating findRatingByRatingId(Long ratingId);

}

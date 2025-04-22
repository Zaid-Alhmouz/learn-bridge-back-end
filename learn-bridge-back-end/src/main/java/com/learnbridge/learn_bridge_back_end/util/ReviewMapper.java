package com.learnbridge.learn_bridge_back_end.util;

import com.learnbridge.learn_bridge_back_end.dto.ReviewDTO;
import com.learnbridge.learn_bridge_back_end.entity.Rating;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ReviewMapper {

    public static ReviewDTO toDTO(Rating rating) {
        return new ReviewDTO(rating);
    }

    public static List<ReviewDTO> toDTOList(List<Rating> ratings) {
        return ratings.stream().map(ReviewMapper::toDTO).collect(Collectors.toList());
    }
}

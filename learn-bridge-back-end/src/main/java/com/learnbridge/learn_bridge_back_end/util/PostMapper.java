package com.learnbridge.learn_bridge_back_end.util;

import com.learnbridge.learn_bridge_back_end.dto.PostDTO;
import com.learnbridge.learn_bridge_back_end.entity.Post;

import java.util.List;
import java.util.stream.Collectors;

public class PostMapper {

    public static PostDTO toDTO(Post post) {

        return new PostDTO(post);
    }

    public static List<PostDTO> toDTOList(List<Post> posts) {

        return posts.stream()
                .map(PostMapper::toDTO)
                .collect(Collectors.toList());
    }
}

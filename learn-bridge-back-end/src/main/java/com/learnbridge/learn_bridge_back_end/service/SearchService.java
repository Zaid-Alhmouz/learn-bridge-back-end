package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.InstructorDAO;
import com.learnbridge.learn_bridge_back_end.dao.PostDAO;
import com.learnbridge.learn_bridge_back_end.dto.PostDTO;
import com.learnbridge.learn_bridge_back_end.dto.SearchDTO;
import com.learnbridge.learn_bridge_back_end.entity.Instructor;
import com.learnbridge.learn_bridge_back_end.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final InstructorDAO instructorDAO;
    private final PostDAO postDAO;

    @Autowired
    public SearchService(InstructorDAO instructorDAO, PostDAO postDAO) {
        this.instructorDAO = instructorDAO;
        this.postDAO = postDAO;
    }

    public List<Instructor> searchInstructors(SearchDTO searchDTO) {
        if (searchDTO.getKeyword() == null || searchDTO.getKeyword().trim().isEmpty()) {
            return instructorDAO.findAllInstructors();
        }
        return instructorDAO.searchInstructors(searchDTO.getKeyword());
    }

    public List<PostDTO> searchPosts(SearchDTO searchDTO) {
        List<Post> posts;

        if (searchDTO.getKeyword() == null || searchDTO.getKeyword().trim().isEmpty()) {
            posts = postDAO.findApprovedPosts();
        } else {
            posts = postDAO.searchPosts(searchDTO.getKeyword());
        }

        // convert Post entities to PostDTO objects
        return posts.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }
}
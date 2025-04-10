package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.PostDAO;
import com.learnbridge.learn_bridge_back_end.dao.UserDAO;
import com.learnbridge.learn_bridge_back_end.dto.CreatePostRequest;
import com.learnbridge.learn_bridge_back_end.entity.Post;
import com.learnbridge.learn_bridge_back_end.entity.PostStatus;
import com.learnbridge.learn_bridge_back_end.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreatePostService {

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private UserDAO userDAO;


    public Post createPost(CreatePostRequest postRequest) {

        User author = userDAO.findUserById(postRequest.getAuthorId());

        if (author == null) {
            throw new RuntimeException("Author not found" + postRequest.getAuthorId());
        }

        // generate a new postId for this author
        Long maxPostId = postDAO.findMaxPostIdByAuthor(author.getId());
        Long nextPostId = (maxPostId == null) ? 1L: maxPostId + 1L;

        Post post = new Post();
        // set fields from the request
        post.setPostId(nextPostId);
        post.setAuthor(author);
        post.setAuthorId(author.getId());


        post.setContent(postRequest.getContent());
        post.setSubject(postRequest.getSubject());
        // set default status to PENDING until the admin checks the post
        post.setPostStatus(PostStatus.PENDING);
        post.setCategory(postRequest.getCategory());
        post.setPrice(postRequest.getPrice());

        // approvalDate will be set later when the post is approved by admin

        // save post tp database
        postDAO.savePost(post);

        return post;
    }
}

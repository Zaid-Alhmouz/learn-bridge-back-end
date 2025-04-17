package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.PostDAO;
import com.learnbridge.learn_bridge_back_end.dao.UserDAO;
import com.learnbridge.learn_bridge_back_end.dto.CreatePostRequest;
import com.learnbridge.learn_bridge_back_end.dto.PostDTO;
import com.learnbridge.learn_bridge_back_end.entity.Post;
import com.learnbridge.learn_bridge_back_end.entity.PostId;
import com.learnbridge.learn_bridge_back_end.entity.PostStatus;
import com.learnbridge.learn_bridge_back_end.entity.User;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.util.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private UserDAO userDAO;


    public Post createPost(CreatePostRequest postRequest, SecurityUser loggedUser) {
        Long authorId = loggedUser.getUser().getId();

        User author = userDAO.findUserById(authorId);
        if (author == null) {
            throw new RuntimeException("Author not found: " + authorId);
        }

        // generate a new postId for this author
        Long maxPostId = postDAO.findMaxPostIdByAuthorId(authorId);
        Long nextPostId = (maxPostId == null) ? 1L : maxPostId + 1L;

        Post post = new Post();
        // Set both components of the composite key
        post.setAuthorId(authorId);
        post.setPostId(nextPostId);

        // Set the associated User (for the join)
        post.setAuthor(author);

        // Set other fields from the request
        post.setContent(postRequest.getContent());
        post.setSubject(postRequest.getSubject());
        post.setPostStatus(PostStatus.PENDING);
        post.setCategory(postRequest.getCategory());
        post.setPrice(postRequest.getPrice());

        // Save the post
        postDAO.savePost(post);

        return post;
    }

    // retrieves posts created by a given author (learner)
    public List<PostDTO> getPostsByAuthorId(Long authorId) {
        List<Post> posts = postDAO.findAllPostsByUserId(authorId);
        return PostMapper.toDTOList(posts);
    }

    // for default posts to be retrieved when the instructor navigates to the posts page, retrieves all posts that suits his favourite category.
    public List<PostDTO> getPostsByFavouriteCategory(Long authorId, String category) {
        List<Post> posts = postDAO.findAllPostsByFavouriteCategory(category);
        return PostMapper.toDTOList(posts);
    }


    // edit existing post by the learner
    public Post editPost(SecurityUser loggedUser, PostDTO editPostRequest) {
        Long authorId = loggedUser.getUser().getId();

        // Validate that the postId is provided
        if (editPostRequest.getPostId() == null) {
            throw new RuntimeException("Post ID must be provided for editing.");
        }

        // Retrieve the post to update using the post ID from the DTO
        PostId postId = new PostId(authorId, editPostRequest.getPostId());
        Post post = postDAO.findPostById(postId);
        if (post == null || !post.getAuthorId().equals(authorId)) {
            throw new RuntimeException("Post not found or you are not the owner of this post.");
        }

        // Update the post fields with the values from the DTO
        post.setContent(editPostRequest.getContent());
        post.setSubject(editPostRequest.getSubject());
        post.setPostStatus(PostStatus.PENDING);
        post.setCategory(editPostRequest.getCategory());
        post.setPrice(editPostRequest.getPrice());

        postDAO.updatePost(post);
        return post;
    }

    public void deletePost(SecurityUser loggedUser, Long postId) {

        Long authorId = loggedUser.getUser().getId();

        PostId compositeId = new PostId(authorId, postId);


       // verify that the post exists and belongs to this user
        Post post = postDAO.findPostById(compositeId);
        if (post == null || !post.getAuthorId().equals(authorId)) {
            throw new RuntimeException("Post not found or you are not the owner of this post.");
        }

        postDAO.deletePost(compositeId);
    }



    // retrieve all pending posts for the admin
    public List<PostDTO> getPendingPosts() {
        List<Post> pendingPosts = postDAO.findAllPendingPosts();
        return PostMapper.toDTOList(pendingPosts);
    }

    // accept pending reports by admin
    public PostDTO acceptPendingPost(SecurityUser loggedUser, PostId compositeId) {

        Post postToBeAccepted = postDAO.findPostById(compositeId);
        if(postToBeAccepted == null) {
            throw new RuntimeException("Post not found");
        }

        else{
            postToBeAccepted.setPostStatus(PostStatus.ACCEPTED);
            postToBeAccepted.setApprovalDate(LocalDateTime.now());
            postDAO.updatePost(postToBeAccepted);
            return PostMapper.toDTO(postToBeAccepted);
        }
    }

    public PostDTO rejectPendingPost(SecurityUser loggedUser, PostId compositeId) {

        Post postToBeRejected = postDAO.findPostById(compositeId);
        if(postToBeRejected == null) {
            throw new RuntimeException("Post not found");
        }

        else{
            postToBeRejected.setPostStatus(PostStatus.REJECTED);
            postToBeRejected.setApprovalDate(LocalDateTime.now());
            postDAO.updatePost(postToBeRejected);
            return PostMapper.toDTO(postToBeRejected);
        }
    }


}

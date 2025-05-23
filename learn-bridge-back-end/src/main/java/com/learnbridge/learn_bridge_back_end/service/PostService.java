package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.*;
import com.learnbridge.learn_bridge_back_end.dto.CreatePostRequest;
import com.learnbridge.learn_bridge_back_end.dto.PostDTO;
import com.learnbridge.learn_bridge_back_end.entity.*;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.util.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LearnerDAO learnerDAO;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AgreementDAO agreementDAO;

    @Autowired
    private SessionDAO sessionDAO;


    // create post by learner
    public Post createPost(CreatePostRequest postRequest, SecurityUser loggedUser) {
        Long authorId = loggedUser.getUser().getId();

        Learner author = learnerDAO.findLearnerById(authorId);
        if (author == null) {
            throw new RuntimeException("Author not found: " + authorId);
        }

        // generate a new postId for this author
        Long maxPostId = postDAO.findMaxPostIdByAuthorId(authorId);
        Long nextPostId = (maxPostId == null) ? 1L : maxPostId + 1L;

        Post post = new Post();
        // set both components of the composite key
        post.setAuthorId(authorId);
        post.setPostId(nextPostId);

        // set the associated User (for the join)
        post.setAuthor(author);

        // set other fields from the request
        post.setContent(postRequest.getContent());
        post.setSubject(postRequest.getSubject());
        post.setPostStatus(PostStatus.PENDING);
        post.setCategory(postRequest.getCategory());
        post.setPrice(postRequest.getPrice());
        post.setSessionDeadline(postRequest.getSessionDeadline());

        // save the post
        postDAO.savePost(post);

        return post;
    }


    // get all posts with not caring for FavouriteCategory
    public List<PostDTO> getAllPosts() {
        List<Post> posts = postDAO.findApprovedPosts();
        if (posts.isEmpty()) return List.of();

        // map entities to DTOs (no images yet)
        List<PostDTO> dtos = PostMapper.toDTOList(posts);

        // for each DTO, fetch the Learner entity to grab the byte[] and Base64‐encode it
        for (PostDTO dto : dtos) {
            Learner author = learnerDAO.findLearnerById(dto.getAuthorId());
            byte[] img = author.getPersonalImage();
            if (img != null && img.length > 0) {
                String b64 = Base64.getEncoder().encodeToString(img);
                // prepend the data-URL header so the front-end can bind directly to <img src=…>
                dto.setAuthorImage("data:image/jpeg;base64," + b64);
            }
        }
        return dtos;
    }

    // retrieves posts created by a given author (learner)
    public List<PostDTO> getPostsByAuthorId(Long authorId) {
        List<Post> posts = postDAO.findAllPostsByUserId(authorId);
        if (posts == null || posts.isEmpty()) {
            return new ArrayList<>();
        }
        return PostMapper.toDTOList(posts);
    }

    public List<PostDTO> getPostsByFavouriteCategory(Long userId, String cat) {
        List<PostDTO> dtos = PostMapper.toDTOList(postDAO.findAllPostsByFavouriteCategory(cat));
        dtos.forEach(dto -> {
            Learner author = learnerDAO.findLearnerById(dto.getAuthorId());
            byte[] img = author.getPersonalImage();
            if (img != null && img.length > 0) {
                String b64 = Base64.getEncoder().encodeToString(img);
                dto.setAuthorImage("data:image/jpeg;base64," + b64);
            }
        });
        return dtos;
    }


    // retrieves all posts that suits selected category.
    public List<PostDTO> getPostsBySelectedCategory(String category) {
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

        // retrieve the post to update using the post ID from the DTO
        PostId postId = new PostId(authorId, editPostRequest.getPostId());
        Post post = postDAO.findPostById(postId);
        if (post == null || !post.getAuthorId().equals(authorId)) {
            throw new RuntimeException("Post not found or you are not the owner of this post.");
        }

        // update the post fields with the values from the DTO
        post.setContent(editPostRequest.getContent());
        post.setSubject(editPostRequest.getSubject());
        post.setPostStatus(editPostRequest.getPostStatus());
        post.setCategory(editPostRequest.getCategory());
        post.setPrice(editPostRequest.getPrice());
        post.setSessionDeadline(editPostRequest.getSessionDeadline());

        postDAO.updatePost(post);
        return post;
    }

    // delete post published by learner
    public void deletePost(SecurityUser loggedUser, Long postId) {
        Long authorId = loggedUser.getUser().getId();
        PostId compositeId = new PostId(authorId, postId);

        // 1) verify ownership
        Post post = postDAO.findPostById(compositeId);
        if (post == null) {
            throw new RuntimeException("Post not found or you are not the owner of this post.");
        }

        // 2) fetch agreements by two keys
        List<Agreement> agreements =
                agreementDAO.findByPost_PostIdAndPost_AuthorId(postId, authorId);

        for (Agreement agr : agreements) {
            Long agrId = agr.getAgreementId();

            // 3) check for any session
            List<Session> sessions = sessionDAO.findByAgreementId(agrId);
            if (sessions != null && !sessions.isEmpty()) {
                throw new RuntimeException(
                        "Cannot delete post because agreement #" + agrId +
                                " already has an active session."
                );
            }

            // 4) safe to delete the agreement
            agreementDAO.deleteAgreement(agrId);
        }

        // 5) delete the post itself
        postDAO.deletePost(compositeId);
    }



    // retrieve all pending posts for the admin
    public List<PostDTO> getPendingPosts() {
        List<Post> pendingPosts = postDAO.findAllPendingPosts();
        if (pendingPosts == null || pendingPosts.isEmpty()) {
            return new ArrayList<>();
        }
        return PostMapper.toDTOList(pendingPosts);
    }

    // accept pending posts by admin
    public PostDTO acceptPendingPost(SecurityUser loggedUser, PostId compositeId) {

        Post postToBeAccepted = postDAO.findPostById(compositeId);

        if(postToBeAccepted == null) {
            throw new RuntimeException("Post not found");
        }

        else{
            User author = userDAO.findUserById(postToBeAccepted.getAuthorId());
            postToBeAccepted.setPostStatus(PostStatus.ACCEPTED);
            postToBeAccepted.setApprovalDate(LocalDateTime.now());
            postDAO.updatePost(postToBeAccepted);
            notificationService.sendAcceptPostNotification(author);
            return PostMapper.toDTO(postToBeAccepted);
        }
    }

    // reject pending posts by admin
    public PostDTO rejectPendingPost(SecurityUser loggedUser, PostId compositeId) {

        Post postToBeRejected = postDAO.findPostById(compositeId);
        if(postToBeRejected == null) {
            throw new RuntimeException("Post not found");
        }

        else
        {
            User author = userDAO.findUserById(postToBeRejected.getAuthorId());
            postToBeRejected.setPostStatus(PostStatus.REJECTED);
            postToBeRejected.setApprovalDate(LocalDateTime.now());
            postDAO.updatePost(postToBeRejected);
            notificationService.sendRejectPostNotification(author);
            return PostMapper.toDTO(postToBeRejected);
        }
    }

    public Post findPostById(PostId id) {
        return postDAO.findPostById(id);
    }

}

package com.learnbridge.learn_bridge_back_end.controller;


import com.learnbridge.learn_bridge_back_end.dao.InstructorDAO;
import com.learnbridge.learn_bridge_back_end.dao.LearnerDAO;
import com.learnbridge.learn_bridge_back_end.dto.CreatePostRequest;
import com.learnbridge.learn_bridge_back_end.dto.PostDTO;
import com.learnbridge.learn_bridge_back_end.entity.*;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.PostService;
import com.learnbridge.learn_bridge_back_end.util.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:4200",
allowCredentials = "true"
) // to connect with angular front end
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private InstructorDAO instructorDAO;

    @Autowired
    private LearnerDAO learnerDAO;

    @PostMapping("/create-post")
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequest postRequest, @AuthenticationPrincipal SecurityUser securityUser) {
    try{
        Post createdPost = postService.createPost(postRequest, securityUser);
        return ResponseEntity.ok(createdPost);
    }
    catch(Exception e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    }

    @GetMapping("/my-posts")
    public ResponseEntity<List<PostDTO>> getMyPosts(@AuthenticationPrincipal SecurityUser loggedUser) {

        // retrieve the logged in user's id from the security context via SecurityUser
        Long userId = loggedUser.getUser().getId();
        List<PostDTO> posts = postService.getPostsByAuthorId(userId);

        if(posts == null || posts.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/favourite-category")
    public ResponseEntity<List<PostDTO>> getPostsByFavoriteCategory(
            @AuthenticationPrincipal SecurityUser user) {
        Long id = user.getUser().getId();
        List<PostDTO> list = postService.getPostsByFavouriteCategory(id,
                instructorDAO.findInstructorById(id).getFavouriteCategory());
        if (list.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/selected-category/{selectedCategory}")
    public ResponseEntity<List<PostDTO>> getFavouriteCategoryPosts(
            @PathVariable String selectedCategory) {

        List<PostDTO> posts = postService.getPostsBySelectedCategory(selectedCategory);
        if (posts == null || posts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Encode and set the authorImage on each DTO
        for (PostDTO dto : posts) {
            Learner author = learnerDAO.findLearnerById(dto.getAuthorId());
            byte[] imgBytes = (author != null) ? author.getPersonalImage() : null;
            if (imgBytes != null && imgBytes.length > 0) {
                String b64 = Base64.getEncoder().encodeToString(imgBytes);
                // prepend data URI header so the front end can bind directly to <img src=…>
                dto.setAuthorImage("data:image/jpeg;base64," + b64);
            }
        }

        return ResponseEntity.ok(posts);
    }


    @PutMapping("/edit-post")
    public ResponseEntity<PostDTO> editPost(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody PostDTO dto) {
        Post updated = postService.editPost(user, dto);
        return ResponseEntity.ok(new PostDTO(updated));
    }

    @DeleteMapping("/{postId}") // http://localhost:8080/api/posts/5
    public ResponseEntity<?> deletePost(@AuthenticationPrincipal SecurityUser loggedUser, @PathVariable Long postId)
    {
        try {
            postService.deletePost(loggedUser, postId);
            return ResponseEntity.ok("Post deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pending-posts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PostDTO>> getPendingPosts(@AuthenticationPrincipal SecurityUser loggedUser) {

        List<PostDTO> pendingPosts = postService.getPendingPosts();
        if(pendingPosts == null || pendingPosts.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        else
            return ResponseEntity.ok(pendingPosts);
    }

    @PutMapping("/accept/{authorId}/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostDTO> acceptPendingPost(
            @AuthenticationPrincipal SecurityUser loggedUser,
            @PathVariable Long authorId,
            @PathVariable Long postId) {

        PostId postIdComposite = new PostId(authorId, postId);

        PostDTO acceptedPost = postService.acceptPendingPost(loggedUser, postIdComposite);

        if(acceptedPost == null){
            return ResponseEntity.notFound().build();
        }
        else
            return ResponseEntity.ok(acceptedPost);
    }



    @PutMapping("/reject/{authorId}/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostDTO> rejectPendingPost(
            @AuthenticationPrincipal SecurityUser loggedUser,
            @PathVariable Long authorId,
            @PathVariable Long postId) {

        PostId postIdComposite = new PostId(authorId, postId);

        PostDTO rejectedPost = postService.rejectPendingPost(loggedUser, postIdComposite);

        if(rejectedPost == null){
            return ResponseEntity.notFound().build();
        }
        else
            return ResponseEntity.ok(rejectedPost);
    }



    @GetMapping("/all-posts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> list = postService.getAllPosts();
        if (list.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(list);
    }


    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPost(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable Long postId) {

        Long authorId = user.getUser().getId();
        PostId id = new PostId(authorId, postId);
        Post post = postService.findPostById(id);

        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new PostDTO(post));
    }
}

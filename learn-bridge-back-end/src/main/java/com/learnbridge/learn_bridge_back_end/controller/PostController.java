package com.learnbridge.learn_bridge_back_end.controller;


import com.learnbridge.learn_bridge_back_end.dao.InstructorDAO;
import com.learnbridge.learn_bridge_back_end.dto.CreatePostRequest;
import com.learnbridge.learn_bridge_back_end.dto.PostDTO;
import com.learnbridge.learn_bridge_back_end.entity.Instructor;
import com.learnbridge.learn_bridge_back_end.entity.Post;
import com.learnbridge.learn_bridge_back_end.entity.PostId;
import com.learnbridge.learn_bridge_back_end.entity.PostStatus;
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
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:4200") // to connect with angular front end
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private InstructorDAO instructorDAO;

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
    public ResponseEntity<List<PostDTO>> getFavouriteCategoryPosts(@AuthenticationPrincipal SecurityUser loggedUser) {

        Long userId = loggedUser.getUser().getId();
        

        Instructor instructor = instructorDAO.findInstructorById(userId);

        String favouriteCategory = instructor.getFavouriteCategory();

        List<PostDTO> favouriteCategoryPosts = postService.getPostsByFavouriteCategory(userId, favouriteCategory);

        if(favouriteCategoryPosts == null || favouriteCategoryPosts.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        else{
            return ResponseEntity.ok(favouriteCategoryPosts);
        }
    }

    @PutMapping("/edit-post")
    public ResponseEntity<?> editPost(@AuthenticationPrincipal SecurityUser loggedUser, @RequestBody PostDTO editPostRequest)
    {
        Post post = postService.editPost(loggedUser, editPostRequest);

        PostDTO updatedPost = PostMapper.toDTO(post);

        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}")
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
}

package com.learnbridge.learn_bridge_back_end.controller;


import com.learnbridge.learn_bridge_back_end.dto.CreatePostRequest;
import com.learnbridge.learn_bridge_back_end.entity.Post;
import com.learnbridge.learn_bridge_back_end.service.CreatePostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:4200") // to connect with angular front end
public class CreatePostController {

    @Autowired
    private CreatePostService createPostService;

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequest postRequest) {
    try{
        Post createdPost = createPostService.createPost(postRequest);
        return ResponseEntity.ok(createdPost);
    }
    catch(Exception e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    }
}

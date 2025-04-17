package com.learnbridge.learn_bridge_back_end.controller;

import com.learnbridge.learn_bridge_back_end.dto.PostDTO;
import com.learnbridge.learn_bridge_back_end.dto.SearchDTO;
import com.learnbridge.learn_bridge_back_end.entity.Instructor;
import com.learnbridge.learn_bridge_back_end.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/search")
@CrossOrigin(origins = "http://localhost:4200")
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    // search instructors with POST request, using the body param
    @PostMapping("/instructors")
    public ResponseEntity<List<Instructor>> searchInstructors(@RequestBody SearchDTO searchDTO) {
        List<Instructor> instructors = searchService.searchInstructors(searchDTO);
        return ResponseEntity.ok(instructors);
    }

    // search instructors with GET request, using URL params
    @GetMapping("/instructors")
    public ResponseEntity<List<Instructor>> searchInstructorsByKeyword(@RequestParam(required = false) String keyword) {
        SearchDTO searchDTO = new SearchDTO(keyword);
        List<Instructor> instructors = searchService.searchInstructors(searchDTO);
        return ResponseEntity.ok(instructors);
    }


    // search posts with POST request, using the body param
    @PostMapping("/posts")
    public ResponseEntity<List<PostDTO>> searchPosts(@RequestBody SearchDTO searchDTO) {
        List<PostDTO> posts = searchService.searchPosts(searchDTO);
        return ResponseEntity.ok(posts);
    }

    // search posts with GET request, using URL params
    @GetMapping("/posts")
    public ResponseEntity<List<PostDTO>> searchPostsByKeyword(@RequestParam(required = false) String keyword) {
        SearchDTO searchDTO = new SearchDTO(keyword);
        List<PostDTO> posts = searchService.searchPosts(searchDTO);
        return ResponseEntity.ok(posts);
    }
}
package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Post;

import java.util.List;

public interface PostDAO {

    void savePost(Post post);
    void updatePost(Post post);
    void deletePost(Long postId);
    Post findPostById(Long postId);
    List<Post> findAllPosts();
    List<Post> findApprovedPosts();
    List<Post> findApprovedPostsByUserId(Long userId);
    List<Post> findPendingPostsByUserId(Long userId);
    List<Post> findApprovedPostsByCategory(String category);
    List<Post> findPendingPostsByCategory(String category);

}

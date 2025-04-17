package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Post;
import com.learnbridge.learn_bridge_back_end.entity.PostId;

import java.util.List;

public interface PostDAO {
    void savePost(Post post);
    void updatePost(Post post);
    void deletePost(PostId postId);
    Post findPostById(PostId postId);
    List<Post> findAllPosts();
    List<Post> findApprovedPosts();
    List<Post> findApprovedPostsByUserId(Long userId);
    List<Post> findPendingPostsByUserId(Long userId);
    List<Post> findAllPostsByUserId(Long userId);
    List<Post> findApprovedPostsByCategory(String category);
    List<Post> findPendingPostsByCategory(String category);
    Long findMaxPostIdByAuthorId(Long authorId);
    List<Post> findAllPostsByFavouriteCategory(String category);
    List<Post> searchPosts(String keyword);
    List<Post> findAllPendingPosts();
}
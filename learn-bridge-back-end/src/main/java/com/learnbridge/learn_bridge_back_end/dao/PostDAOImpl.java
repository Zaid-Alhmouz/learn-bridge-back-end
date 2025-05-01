package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Post;
import com.learnbridge.learn_bridge_back_end.entity.PostId;
import com.learnbridge.learn_bridge_back_end.entity.PostStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PostDAOImpl implements PostDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void savePost(Post post) {
        entityManager.persist(post);
    }

    @Override
    @Transactional
    public void updatePost(Post post) {
        Post postToUpdate = entityManager.find(Post.class, post.getPostId());
        if (postToUpdate != null) {
            entityManager.merge(post);
        }
    }

    @Override
    @Transactional
    public void deletePost(PostId postId) {
        Post post = entityManager.find(Post.class, postId);
        if (post != null) {
            entityManager.remove(entityManager.contains(post) ? post : entityManager.merge(post));
        }
    }

    @Override
    public Post findPostById(PostId postId) {
        return entityManager.find(Post.class, postId);
    }

    @Override
    public List<Post> findAllPosts() {
        return entityManager.createQuery("from Post", Post.class).getResultList();
    }

    @Override
    public List<Post> findApprovedPosts() {
        String sql = "select p from Post p where p.postStatus = :PostStatus";
        Query query = entityManager.createQuery(sql);
        query.setParameter("PostStatus", PostStatus.ACCEPTED);
        return query.getResultList();
    }

    @Override
    public List<Post> findApprovedPostsByUserId(Long userId) {
        String sql = "select p from Post p where p.postStatus = :PostStatus and p.author.learnerId = :UserId";
        Query query = entityManager.createQuery(sql);
        query.setParameter("PostStatus", PostStatus.ACCEPTED);
        query.setParameter("UserId", userId);
        return query.getResultList();
    }

    @Override
    public List<Post> findPendingPostsByUserId(Long userId) {
        String sql = "select p from Post p where p.postStatus = :PostStatus and p.author.learnerId = :UserId";
        Query query = entityManager.createQuery(sql);
        query.setParameter("PostStatus", PostStatus.PENDING);
        query.setParameter("UserId", userId);
        return query.getResultList();
    }

    @Override
    public List<Post> findAllPostsByUserId(Long userId) {
        String sql = "select p from Post p where p.author.learnerId = :UserId";
        Query query = entityManager.createQuery(sql);
        query.setParameter("UserId", userId);
        return query.getResultList();
    }

    @Override
    public List<Post> findApprovedPostsByCategory(String category) {
        String sql = "select p from Post p where p.postStatus = :PostStatus and p.category = :category";
        Query query = entityManager.createQuery(sql);
        query.setParameter("PostStatus", PostStatus.ACCEPTED);
        query.setParameter("category", category);
        return query.getResultList();
    }

    @Override
    public List<Post> findPendingPostsByCategory(String category) {
        String sql = "select p from Post p where p.postStatus = :PostStatus and p.category = :category";
        Query query = entityManager.createQuery(sql);
        query.setParameter("PostStatus", PostStatus.PENDING);
        query.setParameter("category", category);
        return query.getResultList();
    }

    @Override
    public Long findMaxPostIdByAuthorId(Long authorId) {
        String sql = "select MAX(p.postId) from Post p WHERE p.authorId = :authorId";
        Long maxPostId = (Long) entityManager.createQuery(sql, Long.class)
                .setParameter("authorId", authorId)
                .getSingleResult();
        return maxPostId;
    }

    @Override
    public List<Post> findAllPostsByFavouriteCategory(String category) {
        String sql = "select p from Post p where p.category = :category AND p.postStatus = :PostStatus";
        TypedQuery<Post> query = entityManager.createQuery(sql, Post.class);
        query.setParameter("category", category);
        query.setParameter("PostStatus", PostStatus.ACCEPTED);
        if(query.getResultList().isEmpty()){
            return null;
        }
        else
            return query.getResultList();
    }

    @Override
    public List<Post> searchPosts(String keyword) {
        String searchKeyword = "%" + keyword.toLowerCase() + "%";
     
        String sql = "select p from Post p where (lower(p.subject) like :keyword " +
                "or lower(p.content) like :keyword " +
                "or lower(p.category) like :keyword) " +
                "and p.postStatus = :postStatus";

        TypedQuery<Post> query = entityManager.createQuery(sql, Post.class);
        query.setParameter("keyword", searchKeyword);
        query.setParameter("postStatus", PostStatus.ACCEPTED);
        return query.getResultList();
    }

    @Override
    public List<Post> findAllPendingPosts() {

        String sqlStatement = "select p from Post p where p.postStatus = :postStatus";
        TypedQuery<Post> pendingPosts = entityManager.createQuery(sqlStatement, Post.class);
        pendingPosts.setParameter("postStatus", PostStatus.PENDING);
        return pendingPosts.getResultList();
    }
}
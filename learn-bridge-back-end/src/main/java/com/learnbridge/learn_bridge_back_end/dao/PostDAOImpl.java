package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Post;
import com.learnbridge.learn_bridge_back_end.entity.PostStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
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
        entityManager.merge(post);
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        Post post = entityManager.find(Post.class, postId);
        entityManager.remove(entityManager.contains(post) ? post : entityManager.merge(post));
    }

    @Override
    public Post findPostById(Long postId) {
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
        String sql = "select p from Post p where p.postStatus = :PostStatus and p.author.userId = :UserId";
        Query query = entityManager.createQuery(sql);
        query.setParameter("PostStatus", PostStatus.ACCEPTED);
        query.setParameter("UserId", userId);
        return query.getResultList();
    }

    @Override
    public List<Post> findPendingPostsByUserId(Long userId) {
        String sql = "select p from Post p where p.postStatus = :PostStatus and p.author.userId = :UserId";
        Query query = entityManager.createQuery(sql);
        query.setParameter("PostStatus", PostStatus.PENDING);
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
}

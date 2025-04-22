package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Rating;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class RatingDAOImpl implements RatingDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Rating saveRating(Rating rating) {

        entityManager.persist(rating);
        return rating;
    }

    @Override
    @Transactional
    public Rating updateRating(Rating rating) {

        return entityManager.merge(rating);
    }

    @Override
    @Transactional
    public void deleteRating(Long ratingId) {

        Rating rating = entityManager.find(Rating.class, ratingId);
        entityManager.remove(entityManager.contains(rating) ? rating : entityManager.merge(rating));
    }

    @Override
    public List<Rating> findAllRatings() {
        return entityManager.createQuery("from Rating", Rating.class).getResultList();
    }

    @Override
    public Rating findRatingBySessionAndLearnerId(Long sessionId, Long learnerId) {
        String sqlStatement = "select r from Rating r where r.session.sessionId = :sessionId AND r.learner.learnerId = :learnerId";
        TypedQuery<Rating> query = entityManager.createQuery(sqlStatement, Rating.class);
        query.setParameter("sessionId", sessionId);
        query.setParameter("learnerId", learnerId);

        return query.getSingleResult();
    }

    @Override
    public List<Rating> findRatingsByInstructorId(Long instructorId) {

        String sqlStatement = "select r from Rating r where r.instructor.user.userId = :instructorId";
        TypedQuery<Rating> query = entityManager.createQuery(sqlStatement, Rating.class);
        query.setParameter("instructorId", instructorId);
        List<Rating> ratings = query.getResultList();

        return ratings;
    }

    @Override
    @Transactional
    public void deleteRatingBySessionAndLearnerId(Long sessionId, Long learnerId) {
        Rating rating = findRatingBySessionAndLearnerId(sessionId, learnerId);
        entityManager.remove(entityManager.contains(rating) ? rating : entityManager.merge(rating));
    }

    @Override
    public Rating findRatingByRatingId(Long ratingId) {
        return entityManager.find(Rating.class, ratingId);
    }
}

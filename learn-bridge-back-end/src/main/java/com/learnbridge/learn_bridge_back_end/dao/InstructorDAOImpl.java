package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Instructor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class InstructorDAOImpl implements InstructorDAO {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public void saveInstructor(Instructor instructor) {
        entityManager.persist(instructor);
    }

    @Override
    @Transactional
    public void updateInstructor(Instructor instructor) {
        entityManager.merge(instructor);
    }

    @Override
    @Transactional
    public void deleteInstructor(Instructor instructor) {
        entityManager.remove(entityManager.contains(instructor) ? instructor : entityManager.merge(instructor));
    }

    @Override
    public List<Instructor> findAllInstructors() {
        return entityManager.createQuery("from Instructor", Instructor.class).getResultList();
    }

    @Override
    public Instructor findInstructorById(Long instructorId) {
        return entityManager.find(Instructor.class, instructorId);
    }

    @Override
    public List<Instructor> findAllInstructorsByFavouriteCategory(String favouriteCategory) {
        String sqlStatement = "from Instructor i where i.favouriteCategory = :favouriteCategory";
        TypedQuery<Instructor> query = entityManager.createQuery(sqlStatement, Instructor.class);
        query.setParameter("favouriteCategory", favouriteCategory);
        return query.getResultList();
    }

    @Override
    public List<Instructor> searchInstructors(String keyword) {
        String searchKeyword = "%" + keyword.toLowerCase() + "%";
        String sqlStatement = "from Instructor i where lower(i.favouriteCategory) like :keyword " +
                "or lower(i.instructorBio) like :keyword";
        TypedQuery<Instructor> query = entityManager.createQuery(sqlStatement, Instructor.class);
        query.setParameter("keyword", searchKeyword);
        return query.getResultList();
    }
}
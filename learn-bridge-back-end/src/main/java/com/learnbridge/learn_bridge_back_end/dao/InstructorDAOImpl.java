package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Instructor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
}

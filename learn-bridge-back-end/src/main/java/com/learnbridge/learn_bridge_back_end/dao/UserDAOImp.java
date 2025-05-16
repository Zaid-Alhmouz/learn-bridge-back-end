package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.AccountStatus;
import com.learnbridge.learn_bridge_back_end.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDAOImp implements UserDAO {

    // define field for entity manager
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    @Transactional
    public void saveUser(User user) {
        entityManager.persist(user);
    }


    @Override
    @Transactional
    public void updateUser(User user) {
        entityManager.merge(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = entityManager.find(User.class, userId);
        entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
    }

    @Override
    public User findUserByEmail(String email) {
        String sqlStatement = "SELECT u FROM User u WHERE u.email = :email";
        TypedQuery<User> query = entityManager.createQuery(sqlStatement, User.class);
        query.setParameter("email", email);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User findUserById(Long userId) {
        return entityManager.find(User.class, userId);
    }

    @Override
    public List<User> findAllUsers() {
       return entityManager.createQuery("from User", User.class).getResultList();
    }

    @Override
    public List<User> findBlockedUsers() {

       String sqlStatement = "SELECT u FROM User u WHERE u.accountStatus = :accountStatus";
       TypedQuery<User> query = entityManager.createQuery(sqlStatement, User.class);
       query.setParameter("accountStatus", AccountStatus.BLOCKED);
       if (query.getResultList().isEmpty()) {
           return new ArrayList<User>();
       }
       return query.getResultList();
    }
}

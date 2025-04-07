package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Admin;
import com.learnbridge.learn_bridge_back_end.entity.Learner;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class AdminDAOImpl implements AdminDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void saveAdmin(Admin admin) {
        entityManager.persist(admin);
    }

    @Override
    @Transactional
    public void updateAdmin(Admin admin) {
        entityManager.merge(admin);
    }

    @Override
    @Transactional
    public void deleteAdmin(Long adminId) {
        Admin admin = entityManager.find(Admin.class, adminId);
        entityManager.remove(entityManager.contains(admin) ? admin: entityManager.merge(admin));
    }

    @Override
    public Admin getAdminById(Long adminId) {
        return entityManager.find(Admin.class, adminId);
    }

    @Override
    public Admin getAdminByFullName(String firstName, String lastName) {
        String sqlStatement = "select a from Admin a where a.firstName = :firstName and a.lastName = :lastName";
        TypedQuery<Admin> query = entityManager.createQuery(sqlStatement, Admin.class);
        query.setParameter("firstName", firstName);
        query.setParameter("lastName", lastName);
        Admin admin = query.getSingleResult();
        return admin;
    }

    @Override
    public List<Admin> findAllAdmins() {

        return entityManager.createQuery("from Admin", Admin.class).getResultList();
    }
}

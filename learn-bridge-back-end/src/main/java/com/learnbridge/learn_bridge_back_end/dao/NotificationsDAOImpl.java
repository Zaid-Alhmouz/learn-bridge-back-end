package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Notifications;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class NotificationsDAOImpl implements NotificationsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void saveNotification(Notifications notification) {
        entityManager.persist(notification);
    }

    @Override
    @Transactional
    public void updateNotification(Notifications notification) {

        entityManager.merge(notification);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        Notifications notification = entityManager.find(Notifications.class, notificationId);
        entityManager.remove(entityManager.contains(notification) ? notification : entityManager.merge(notification));
    }

    @Override
    public List<Notifications> findAllNotifications() {

        return entityManager.createQuery("from Notifications", Notifications.class).getResultList();
    }

    @Override
    @Transactional
    public void deleteNotificationsByUserId(Long userId) {

        List<Notifications> notificationsToBeDeleted = findNotificationByUserId(userId);

        for (Notifications notification : notificationsToBeDeleted) {
            deleteNotification(notification.getNotificationId());
        }
    }

    @Override
    public List<Notifications> findNotificationByUserId(Long userId) {

        String sqlStatement = "from Notifications n where n.user.userId = :userId";
        TypedQuery<Notifications> query = entityManager.createQuery(sqlStatement, Notifications.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public Notifications findNotificationById(Long notificationId) {
        return entityManager.find(Notifications.class, notificationId);
    }
}

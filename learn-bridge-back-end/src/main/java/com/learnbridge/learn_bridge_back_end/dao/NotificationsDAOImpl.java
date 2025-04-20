package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Agreement;
import com.learnbridge.learn_bridge_back_end.entity.Notifications;
import com.learnbridge.learn_bridge_back_end.entity.ReadStatus;
import com.learnbridge.learn_bridge_back_end.entity.User;
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
    public Notifications saveNotification(Notifications notification) {
        entityManager.persist(notification);
        return notification;
    }

    @Override
    @Transactional
    public Notifications updateNotification(Notifications notification) {
        return entityManager.merge(notification);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        Notifications notification = entityManager.find(Notifications.class, notificationId);
        if (notification != null) {
            entityManager.remove(entityManager.contains(notification) ? notification : entityManager.merge(notification));
        }
    }

    @Override
    public List<Notifications> findAllNotifications() {
        return entityManager.createQuery("from Notifications", Notifications.class).getResultList();
    }

    @Override
    @Transactional
    public void deleteNotificationsByUserId(Long userId) {
        List<Notifications> notificationsToBeDeleted = findNotificationsByUserId(userId);
        for (Notifications notification : notificationsToBeDeleted) {
            deleteNotification(notification.getNotificationId());
        }
    }

    public List<Notifications> findNotificationsByUserId(Long userId) {
        String sqlStatement = "from Notifications n where n.user.userId = :userId";
        TypedQuery<Notifications> query = entityManager.createQuery(sqlStatement, Notifications.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<Notifications> findNotificationsByUserId(User user) {
        TypedQuery<Notifications> query = entityManager.createQuery(
                "SELECT n FROM Notifications n WHERE n.user = :user ORDER BY n.timestamp DESC",
                Notifications.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    @Override
    public List<Notifications> findNotificationsByAgreement(Agreement agreement) {
        TypedQuery<Notifications> query = entityManager.createQuery(
                "SELECT n FROM Notifications n WHERE n.agreement = :agreement",
                Notifications.class);
        query.setParameter("agreement", agreement);
        return query.getResultList();
    }

    @Override
    public List<Notifications> findUnreadNotificationsByUser(User user) {
        TypedQuery<Notifications> query = entityManager.createQuery(
                "SELECT n FROM Notifications n WHERE n.user = :user AND n.readStatus = :readStatus ORDER BY n.timestamp DESC",
                Notifications.class);
        query.setParameter("user", user);
        query.setParameter("readStatus", ReadStatus.UNREAD);
        return query.getResultList();
    }

    @Override
    public Notifications findNotificationById(Long notificationId) {
        return entityManager.find(Notifications.class, notificationId);
    }
}
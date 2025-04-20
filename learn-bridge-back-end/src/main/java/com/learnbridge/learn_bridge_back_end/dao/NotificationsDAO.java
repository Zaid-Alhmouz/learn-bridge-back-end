package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Agreement;
import com.learnbridge.learn_bridge_back_end.entity.Notifications;
import com.learnbridge.learn_bridge_back_end.entity.User;

import java.util.List;

public interface NotificationsDAO {
    Notifications saveNotification(Notifications notification);
    Notifications updateNotification(Notifications notification);
    void deleteNotification(Long notificationId);
    List<Notifications> findAllNotifications();
    void deleteNotificationsByUserId(Long userId);
    List<Notifications> findNotificationsByUserId(User user);
    List<Notifications> findNotificationsByAgreement(Agreement agreement);
    List<Notifications> findUnreadNotificationsByUser(User user);
    Notifications findNotificationById(Long notificationId);
}
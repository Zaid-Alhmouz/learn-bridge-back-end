package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Notifications;

import java.util.List;

public interface NotificationsDAO {

    void saveNotification(Notifications notification);
    void updateNotification(Notifications notification);
    void deleteNotification(Long notificationId);
    List<Notifications> findAllNotifications();
    void deleteNotificationsByUserId(Long userId);
    List<Notifications> findNotificationByUserId(Long userId);
    Notifications findNotificationById(Long notificationId);

}

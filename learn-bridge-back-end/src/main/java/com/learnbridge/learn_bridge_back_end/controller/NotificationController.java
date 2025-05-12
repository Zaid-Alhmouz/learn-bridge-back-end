package com.learnbridge.learn_bridge_back_end.controller;

import com.learnbridge.learn_bridge_back_end.dto.NotificationDTO;
import com.learnbridge.learn_bridge_back_end.entity.Notifications;
import com.learnbridge.learn_bridge_back_end.entity.User;
import com.learnbridge.learn_bridge_back_end.security.SecurityUser;
import com.learnbridge.learn_bridge_back_end.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {


    @Autowired
    private NotificationService notificationsService;

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(@AuthenticationPrincipal SecurityUser securityUser) {
        User user = securityUser.getUser();
        List<NotificationDTO> notifications = notificationsService.getUnreadUserNotifications(user);
        return ResponseEntity.ok(notifications);
    }


    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationsService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-notification/{notificationId}")
    public ResponseEntity<NotificationDTO> deleteNotification(@PathVariable Long notificationId) {
        NotificationDTO notificationToBeDeleted = notificationsService.deleteNotificationById(notificationId);
        if (notificationToBeDeleted == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(notificationToBeDeleted, HttpStatus.OK);
    }
}

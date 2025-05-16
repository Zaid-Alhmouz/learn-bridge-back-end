package com.learnbridge.learn_bridge_back_end.service;

import com.learnbridge.learn_bridge_back_end.dao.UserDAO;
import com.learnbridge.learn_bridge_back_end.dto.UserDTO;
import com.learnbridge.learn_bridge_back_end.entity.AccountStatus;
import com.learnbridge.learn_bridge_back_end.entity.User;
import com.learnbridge.learn_bridge_back_end.util.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserDAO userDAO;

    @Transactional
    public UserDTO blockUser(Long userId) {
        User u = userDAO.findUserById(userId);
        if (u == null) throw new EntityNotFoundException("User not found: " + userId);
        u.setAccountStatus(AccountStatus.BLOCKED);
        userDAO.updateUser(u);
        return UserMapper.toUserDTO(u);
    }

    // unblock user
    public UserDTO unblockUser(Long userId) {
        User user = userDAO.findUserById(userId);
        if (user == null) throw new EntityNotFoundException("User not found: " + userId);

        if (user.getAccountStatus() != AccountStatus.BLOCKED) throw new EntityNotFoundException("User is not blocked");

        user.setAccountStatus(AccountStatus.ACTIVE);

        userDAO.updateUser(user);
        return UserMapper.toUserDTO(user);
    }

    // get all blocked users
    public List<UserDTO> getBlockedUsers() {
        List<User> blockedUsers = userDAO.findBlockedUsers();
        if (blockedUsers == null) return new ArrayList<>();

        return UserMapper.toUserDTOs(blockedUsers);
    }
}

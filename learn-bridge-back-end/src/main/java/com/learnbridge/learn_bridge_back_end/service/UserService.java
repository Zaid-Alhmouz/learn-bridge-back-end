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
}

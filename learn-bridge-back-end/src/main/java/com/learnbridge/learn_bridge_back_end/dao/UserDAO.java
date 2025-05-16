package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.User;

import java.util.List;

public interface UserDAO {


     void saveUser(User user);

     void updateUser(User user);

     void deleteUser(Long userId);

     User findUserByEmail(String email);

     User findUserById(Long userId);

     List<User> findAllUsers();

     List<User> findBlockedUsers();
}

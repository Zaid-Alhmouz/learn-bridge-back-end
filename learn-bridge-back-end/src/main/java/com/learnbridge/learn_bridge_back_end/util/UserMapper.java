package com.learnbridge.learn_bridge_back_end.util;

import com.learnbridge.learn_bridge_back_end.dto.UserDTO;
import com.learnbridge.learn_bridge_back_end.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user);
    }

    public static List<UserDTO> toUserDTOs(List<User> users) {
        return users.stream().map(UserMapper::toUserDTO).collect(Collectors.toList());
    }
}

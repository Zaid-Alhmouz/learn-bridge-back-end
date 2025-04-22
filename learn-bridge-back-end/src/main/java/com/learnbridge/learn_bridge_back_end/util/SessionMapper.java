package com.learnbridge.learn_bridge_back_end.util;

import com.learnbridge.learn_bridge_back_end.dto.SessionDTO;
import com.learnbridge.learn_bridge_back_end.entity.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SessionMapper {

    public static SessionDTO toSessionDTO(Session session) {
        return new SessionDTO(session);
    }

    public static List<SessionDTO> toSessionDTOList(List<Session> sessionList) {
        return sessionList.stream().map(SessionMapper::toSessionDTO).collect(Collectors.toList());
    }
}

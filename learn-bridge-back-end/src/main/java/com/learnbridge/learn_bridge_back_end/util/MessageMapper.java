package com.learnbridge.learn_bridge_back_end.util;

import com.learnbridge.learn_bridge_back_end.dto.MessageDTO;
import com.learnbridge.learn_bridge_back_end.entity.TextMessage;

import java.util.List;
import java.util.stream.Collectors;

public class MessageMapper {

    public static MessageDTO toMessageDTO(TextMessage textMessage) {
        return new MessageDTO(textMessage);
    }

    public static List<MessageDTO> toMessageDTOList(List<TextMessage> textMessages) {
        return textMessages.stream().map(MessageMapper::toMessageDTO).collect(Collectors.toList());
    }
}

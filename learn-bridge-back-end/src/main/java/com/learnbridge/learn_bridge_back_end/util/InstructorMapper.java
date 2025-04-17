package com.learnbridge.learn_bridge_back_end.util;

import com.learnbridge.learn_bridge_back_end.dto.InstructorDTO;
import com.learnbridge.learn_bridge_back_end.entity.Instructor;

import java.util.List;
import java.util.stream.Collectors;

public class InstructorMapper {

    public static InstructorDTO toDTO(Instructor instructor) {

        return new InstructorDTO(instructor);
    }

    public static List<InstructorDTO> toDTOList(List<Instructor> instructors) {

        return instructors.stream().map(InstructorMapper::toDTO).collect(Collectors.toList());
    }
}

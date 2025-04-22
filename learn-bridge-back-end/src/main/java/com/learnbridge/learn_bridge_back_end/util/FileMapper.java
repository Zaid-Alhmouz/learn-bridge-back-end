package com.learnbridge.learn_bridge_back_end.util;

import com.learnbridge.learn_bridge_back_end.dto.FileDTO;
import com.learnbridge.learn_bridge_back_end.entity.File;

import java.util.List;
import java.util.stream.Collectors;

public class FileMapper {

    public static FileDTO toFileDTO(File file) {
        return new FileDTO(file);
    }

    public static List<FileDTO> toFileDTO(List<File> files) {
        return files.stream().map(FileMapper::toFileDTO).collect(Collectors.toList());
    }
}

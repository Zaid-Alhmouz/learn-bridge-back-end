package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.File;

import java.util.List;

public interface FileDAO {

    void saveFile(File file);
    void updateFile(File file);
    void deleteFileById(Long fileId);
    void deleteFilesByChatId(Long chatId);
    File findFileById(Long fileId);
    List<File> findFilesByChatId(Long chatId);
    List<File> findAllFiles();
}

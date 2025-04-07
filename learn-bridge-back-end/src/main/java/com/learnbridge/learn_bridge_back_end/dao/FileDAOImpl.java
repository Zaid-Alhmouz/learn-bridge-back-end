package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.File;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class FileDAOImpl implements FileDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void saveFile(File file) {

        entityManager.persist(file);
    }

    @Override
    @Transactional
    public void updateFile(File file) {

        entityManager.merge(file);
    }

    @Override
    @Transactional
    public void deleteFileById(Long fileId) {

        File file = entityManager.find(File.class, fileId);
        entityManager.remove(entityManager.contains(file) ? file : entityManager.merge(file));
    }

    @Override
    @Transactional
    public void deleteFilesByChatId(Long chatId) {

        List<File> files = findFilesByChatId(chatId);

        for (File file : files) {
            entityManager.remove(entityManager.contains(file) ? file : entityManager.merge(file));
        }

    }

    @Override
    public File findFileById(Long fileId) {

        return entityManager.find(File.class, fileId);
    }

    @Override
    public List<File> findFilesByChatId(Long chatId) {
        String sql = "Select f from File f where f.chat.chatId = :chatId";
        TypedQuery<File> query = entityManager.createQuery(sql, File.class);
        query.setParameter("chatId", chatId);
        return query.getResultList();
    }

    @Override
    public List<File> findAllFiles() {
        return entityManager.createQuery("select f from File f", File.class).getResultList();
    }
}

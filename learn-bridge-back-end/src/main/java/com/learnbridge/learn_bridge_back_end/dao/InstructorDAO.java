package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Instructor;
import java.util.List;

public interface InstructorDAO {
    void saveInstructor(Instructor instructor);
    void updateInstructor(Instructor instructor);
    void deleteInstructor(Instructor instructor);
    List<Instructor> findAllInstructors();
    Instructor findInstructorById(Long instructorId);
    List<Instructor> findAllInstructorsByFavouriteCategory(String favouriteCategory);
    List<Instructor> searchInstructors(String keyword);
}
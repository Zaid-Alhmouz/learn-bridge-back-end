package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Learner;

import java.util.List;

public interface LearnerDAO {

    void saveLearner(Learner learner);
    void updateLearner(Learner learner);
    void deleteLearner(Long learnerId);
    Learner findLearnerById(Long learnerId);
    List<Learner> findAllLearners();
}

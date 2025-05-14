package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.*;

import java.util.List;

public interface AgreementDAO {

    Agreement saveAgreement(Agreement agreement);
    Agreement updateAgreement(Agreement agreement);
    Agreement findAgreementById(Long agreementId);
    void deleteAgreement(Long agreementId);
    List<Agreement> findAllAgreements();
    List<Agreement> findAgreementByLearnerId(Long learnerId);
    List<Agreement> findAgreementByInstructorId(Long instructorId);
    List<Agreement> findAgreementByPostId(PostId postId);
    List<Agreement> findByPost_PostIdAndPost_AuthorId(Long postId, Long authorId);
}

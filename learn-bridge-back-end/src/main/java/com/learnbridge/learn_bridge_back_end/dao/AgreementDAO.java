package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Agreement;

import java.util.List;

public interface AgreementDAO {

    void saveAgreement(Agreement agreement);
    void updateAgreement(Agreement agreement);
    Agreement findAgreement(Long agreementId);
    void deleteAgreement(Long agreementId);
    List<Agreement> findAllAgreements();
}

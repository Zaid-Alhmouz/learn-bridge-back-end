package com.learnbridge.learn_bridge_back_end.dto;

import com.learnbridge.learn_bridge_back_end.entity.Agreement;

public class AgreementRequestDTO {

    private Long instructorId;
    private Long postId;

    public AgreementRequestDTO() {}

    public AgreementRequestDTO(Agreement agreement) {
        this.instructorId = agreement.getInstructor().getInstructorId();
        this.postId = agreement.getPost().getPostId();
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "AgreementRequestDTO{" +
                "instructorId=" + instructorId +
                ", postId=" + postId +
                '}';
    }
}

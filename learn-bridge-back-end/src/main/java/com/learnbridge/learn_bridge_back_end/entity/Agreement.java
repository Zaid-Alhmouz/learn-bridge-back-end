package com.learnbridge.learn_bridge_back_end.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "agreement")
public class Agreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agreement_id")
    private Long agreementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learner_id", referencedColumnName = "learner_id")
    private Learner learner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", referencedColumnName = "instructor_id")
    private Instructor instructor;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name="post_author_id", referencedColumnName="author_id", nullable=true),
            @JoinColumn(name="post_id", referencedColumnName="post_id", nullable=true)
    })
    private Post post;

    @Column(name="price", precision=6, scale=2)
    private BigDecimal price;


    public Long getAgreementId() {
        return agreementId;
    }


    public Learner getLearner() {
        return learner;
    }

    public void setLearner(Learner learner) {
        this.learner = learner;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setAgreementId(Long agreementId) {
        this.agreementId = agreementId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Agreement{" +
                "agreementId=" + agreementId +
                ", learner=" + learner +
                ", instructor=" + instructor +
                ", post=" + post +
                '}';
    }
}

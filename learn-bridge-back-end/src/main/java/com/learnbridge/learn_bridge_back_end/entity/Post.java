package com.learnbridge.learn_bridge_back_end.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "post")
@IdClass(PostId.class)
public class Post {

    @Id
    @Column(name = "author_id")
    private Long authorId;

    @Id
    @Column(name = "post_id")
    private Long postId;

    @MapsId("authorId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "learner_id", insertable = false, updatable = false)
    private Learner author;

    @Column(name = "approval_date", nullable = true)
    private LocalDateTime approvalDate;

    @Column(name = "price",nullable = false, precision = 6, scale = 2)
    private BigDecimal price;

    @Column(name = "subject", nullable = false, length = 100)
    private String subject;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_status", nullable = false, length = 50)
    private PostStatus postStatus;

    @Column(name = "category", nullable = false, length = 50)
    private String category;


    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Learner getAuthor() {
        return author;
    }

    public void setAuthor(Learner user) {
        this.author = user;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PostStatus getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(PostStatus postStatus) {
        this.postStatus = postStatus;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Post{" +
                "post_id=" + postId +
                ", authorId=" + author.getUser().getId() +
                ", approvalDate=" + approvalDate +
                ", price=" + price +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", postStatus=" + postStatus +
                ", category='" + category + '\'' +
                '}';
    }
}

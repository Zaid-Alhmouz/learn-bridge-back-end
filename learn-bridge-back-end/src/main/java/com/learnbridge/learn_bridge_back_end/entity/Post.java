package com.learnbridge.learn_bridge_back_end.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "post")
@IdClass(PostId.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long postId;

    @Id
    @Column(name = "author_id")
    private Long authorId;

    @MapsId("authorId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User author;

    @Column(name = "approval_date", nullable = false)
    private LocalDate approvalDate;

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

    public Long getPostId() {
        return postId;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User user) {
        this.author = user;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDate approvalDate) {
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
                ", authorId=" + author.getId() +
                ", approvalDate=" + approvalDate +
                ", price=" + price +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", postStatus=" + postStatus +
                ", category='" + category + '\'' +
                '}';
    }
}

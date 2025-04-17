package com.learnbridge.learn_bridge_back_end.dto;

import com.learnbridge.learn_bridge_back_end.entity.Post;
import com.learnbridge.learn_bridge_back_end.entity.PostStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PostDTO {

    private Long postId;
    private Long authorId;
    private String authorName;
    private LocalDateTime approvalDate;
    private BigDecimal price;
    private String subject;
    private String content;
    private PostStatus postStatus;
    private String category;

    public PostDTO() {}

    public PostDTO(Post post) {
        this.postId = post.getPostId();
        this.authorId = post.getAuthorId();

        if(post.getAuthor() != null) {
            this.authorName = post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName();
        }

        this.approvalDate = post.getApprovalDate();
        this.price = post.getPrice();
        this.subject = post.getSubject();
        this.content = post.getContent();
        this.postStatus = post.getPostStatus();
        this.category = post.getCategory();
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
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
}

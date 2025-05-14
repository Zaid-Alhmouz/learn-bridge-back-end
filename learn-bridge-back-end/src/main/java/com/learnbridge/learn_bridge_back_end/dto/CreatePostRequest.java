package com.learnbridge.learn_bridge_back_end.dto;


import java.math.BigDecimal;
import java.time.LocalDate;

public class CreatePostRequest {


    private String subject;
    private String content;
    private String category;
    private BigDecimal price;
    private LocalDate sessionDeadline;




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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getSessionDeadline() { return sessionDeadline; }
    public void setSessionDeadline(LocalDate sessionDeadline) {
        this.sessionDeadline = sessionDeadline;
    }
}

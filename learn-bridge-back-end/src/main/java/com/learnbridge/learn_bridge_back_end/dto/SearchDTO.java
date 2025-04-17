package com.learnbridge.learn_bridge_back_end.dto;

public class SearchDTO {
    private String keyword;

    public SearchDTO() {
    }

    public SearchDTO(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "SearchDTO{" +
                "keyword='" + keyword + '\'' +
                '}';
    }
}
package com.learnbridge.learn_bridge_back_end.entity;

import java.io.Serializable;
import java.util.Objects;

public class PostId implements Serializable {
    private Long postId;
    private Long authorId;

    public PostId() {}

    public PostId(Long postId, Long authorId) {
        this.postId = postId;
        this.authorId = authorId;
    }

    // Required getters
    public Long getPostId() { return postId; }
    public Long getAuthorId() { return authorId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostId postId1 = (PostId) o;
        return Objects.equals(postId, postId1.postId) &&
                Objects.equals(authorId, postId1.authorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, authorId);
    }
}
package com.learnbridge.learn_bridge_back_end.entity;

import java.io.Serializable;
import java.util.Objects;

public class PostId implements Serializable {
    private Long authorId;
    private Long postId;

    public PostId() {}

    public PostId(Long authorId, Long postId) {
        this.authorId = authorId;
        this.postId = postId;
    }

    public Long getAuthorId() { return authorId; }
    public Long getPostId() { return postId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostId)) return false;
        PostId that = (PostId) o;
        return Objects.equals(authorId, that.authorId) &&
                Objects.equals(postId, that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorId, postId);
    }
}

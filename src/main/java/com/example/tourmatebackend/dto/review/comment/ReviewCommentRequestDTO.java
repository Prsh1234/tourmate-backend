package com.example.tourmatebackend.dto.review.comment;

public class ReviewCommentRequestDTO {
    private String comment;

    public ReviewCommentRequestDTO() {}

    public ReviewCommentRequestDTO(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }
}

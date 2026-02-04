package com.example.tourmatebackend.dto.review;


import com.example.tourmatebackend.model.Tour;

public class ReviewResponseDTO {

    private int id;
    private int rating;
    private String reviewText;

    private int reviewerId;
    private String reviewerName;
    private byte[] reviewerProfilePic;

    private String reviewForType;  // "GUIDE" or "TOUR"
    private int reviewForId;

    private String createdAt;

    private String guideComment;
    private String commentAt;
    private Tour tour;

    // -------- Constructor --------
    public ReviewResponseDTO(int id, int rating, String reviewText,
                             int reviewerId, String reviewerName, byte[] reviewerProfilePic,
                             String reviewForType, int reviewForId,
                             String createdAt, String guideComment,String commentAt) {

        this.id = id;
        this.rating = rating;
        this.reviewText = reviewText;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName;
        this.reviewerProfilePic = reviewerProfilePic;
        this.reviewForType = reviewForType;
        this.reviewForId = reviewForId;
        this.createdAt = createdAt;
        this.guideComment = guideComment;
        this.commentAt = commentAt;
    }

    // -------- GETTERS ----------

    public Tour getTour() {
        return tour;
    }
    public void setTour(Tour tour) {
        this.tour = tour;
    }
    public int getId() { return id; }
    public int getRating() { return rating; }
    public String getReviewText() { return reviewText; }
    public int getReviewerId() { return reviewerId; }
    public String getReviewerName() { return reviewerName; }
    public byte[] getReviewerProfilePic() { return reviewerProfilePic; }
    public String getReviewForType() { return reviewForType; }
    public int getReviewForId() { return reviewForId; }
    public String getCreatedAt() { return createdAt; }
    public String getGuideComment() {return guideComment;}
    public String  getCommentAt() {return commentAt;}
}

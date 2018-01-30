package com.hoperrush.pojo;

/**
 * Created by user145 on 2/14/2017.
 */
public class ReviewPojoInfo {

    private String ReviewBookingId = "";
    private String ReviewCategory = "";
    private String ReviewTasker = "";
    private String ReviewRating = "";
    private String ReviewDate = "";
    private String ReviewComments = "";
    private String ReviewImage = "";
    private String ReviewTaskerImage = "";


    public String getReviewImage() {
        return ReviewImage;
    }

    public void setReviewImage(String reviewImage) {
        ReviewImage = reviewImage;
    }

    public String getReviewTaskerImage() {
        return ReviewTaskerImage;
    }

    public void setReviewTaskerImage(String reviewTaskerImage) {
        ReviewTaskerImage = reviewTaskerImage;
    }

    public String getReviewBookingId() {
        return ReviewBookingId;
    }

    public void setReviewBookingId(String reviewBookingId) {
        ReviewBookingId = reviewBookingId;
    }

    public String getReviewComments() {
        return ReviewComments;
    }

    public void setReviewComments(String reviewComments) {
        ReviewComments = reviewComments;
    }

    public String getReviewTasker() {
        return ReviewTasker;
    }

    public void setReviewTasker(String reviewTasker) {
        ReviewTasker = reviewTasker;
    }

    public String getReviewCategory() {
        return ReviewCategory;
    }

    public void setReviewCategory(String reviewCategory) {
        ReviewCategory = reviewCategory;
    }

    public String getReviewRating() {
        return ReviewRating;
    }

    public void setReviewRating(String reviewRating) {
        ReviewRating = reviewRating;
    }

    public String getReviewDate() {
        return ReviewDate;
    }

    public void setReviewDate(String reviewDate) {
        ReviewDate = reviewDate;
    }
}

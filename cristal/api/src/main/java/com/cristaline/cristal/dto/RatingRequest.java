package com.cristaline.cristal.dto;

public class RatingRequest {
    private Double rating;

    public RatingRequest() {}

    public RatingRequest(Double rating) {
        this.rating = rating;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}

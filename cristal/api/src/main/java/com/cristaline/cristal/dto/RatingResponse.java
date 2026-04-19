package com.cristaline.cristal.dto;

public class RatingResponse {
    private Long id;
    private Double rating;
    private String ratedAt;

    public RatingResponse() {}

    public RatingResponse(Long id, Double rating, String ratedAt) {
        this.id = id;
        this.rating = rating;
        this.ratedAt = ratedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getRatedAt() {
        return ratedAt;
    }

    public void setRatedAt(String ratedAt) {
        this.ratedAt = ratedAt;
    }
}

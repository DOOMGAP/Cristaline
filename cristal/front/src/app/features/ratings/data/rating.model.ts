export interface Rating {
  id: number;
  rating: number;
  ratedAt: string;
}

export interface RatingRequest {
  rating: number;
}

export interface RatingSummary {
  averageRating: number | null;
  ratingsCount: number;
}

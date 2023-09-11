package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    Review createReview(Review review);

    Review getReviewById(int reviewId);

    Review updateReview(Review review);

    void deleteReviewById(int reviewId);

    void likeReview(int reviewId, int userId);

    void dislikeReview(int reviewId, int userId);

    void deleteLikeReview(int reviewId, int userId);

    void deleteDislikeReview(int reviewId, int userId);

    List<Review> getReview(int filmId, int count);
}

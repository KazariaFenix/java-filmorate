package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review createReview(Review review);


    Review getReviewById(int reviewId);

    List<Review> getReview(Integer count);

    List<Review> getReviewByFilmId(int filmId, int count);

    Review updateReview(Review review);

    void deleteReviewById(int reviewId);

    void likeReview(int reviewId, int userId);

    void dislikeReview(int reviewId, int userId);

    void deleteLikeReview(int reviewId, int userId);

    void deleteDislikeReview(int reviewId, int userId);


}

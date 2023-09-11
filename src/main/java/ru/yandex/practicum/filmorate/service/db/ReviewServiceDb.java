package ru.yandex.practicum.filmorate.service.db;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceDb implements ReviewService {
    private final ReviewStorage reviewStorage;

    @Override
    public Review createReview(Review review) {
        return reviewStorage.createReview(review);
    }

    @Override
    public Review getReviewById(int reviewId) {
        return reviewStorage.getReviewById(reviewId);
    }

    @Override
    public Review updateReview(Review review) {
        return reviewStorage.updateReview(review);
    }

    @Override
    public void deleteReviewById(int reviewId) {
        reviewStorage.deleteReviewById(reviewId);
    }

    @Override
    public void likeReview(int reviewId, int userId) {
        reviewStorage.likeReview(reviewId, userId);
    }

    @Override
    public void dislikeReview(int reviewId, int userId) {
        reviewStorage.dislikeReview(reviewId, userId);
    }

    @Override
    public void deleteLikeReview(int reviewId, int userId) {
        reviewStorage.deleteLikeReview(reviewId, userId);
    }

    @Override
    public void deleteDislikeReview(int reviewId, int userId) {
        reviewStorage.deleteDislikeReview(reviewId, userId);
    }

    @Override
    public List<Review> getReview(Integer filmId, Integer count) {
        if (count == null || count == 0) {
            count = 10;
        }
        if (filmId == null || filmId == 0) {
            return reviewStorage.getReview(count);
        } else {
            return reviewStorage.getReviewByFilmId(filmId, count);
        }
    }
}

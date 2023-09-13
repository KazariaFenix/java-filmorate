package ru.yandex.practicum.filmorate.service.db;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.EventStatus;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceDb implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final EventStorage eventStorage;

    @Override
    public Review createReview(Review review) {
        Review rev =  reviewStorage.createReview(review);
        eventStorage.addEvent(rev.getReviewId(), rev.getUserId(), EventType.REVIEW, EventStatus.ADD);
        return rev;
    }

    @Override
    public Review getReviewById(int reviewId) {
        return reviewStorage.getReviewById(reviewId);
    }

    @Override
    public Review updateReview(Review review) {
        Review rev =  reviewStorage.updateReview(review);
        eventStorage.addEvent(rev.getReviewId(), rev.getUserId(), EventType.REVIEW, EventStatus.UPDATE);
        return rev;
    }

    @Override
    public void deleteReviewById(int reviewId) {
        Review rev = reviewStorage.getReviewById(reviewId);
        eventStorage.addEvent(rev.getReviewId(), rev.getUserId(), EventType.REVIEW, EventStatus.REMOVE);
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

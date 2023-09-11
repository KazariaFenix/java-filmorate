package ru.yandex.practicum.filmorate.service.db;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceDb implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

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
    public List<Review> getReview(int filmId, int count) {
        if (count == 0) {
            count = 10;
        }
        if (filmId == 0) {
            return reviewStorage.getReview(count);
        } else {
            return reviewStorage.getReviewByFilmId(filmId, count);
        }

    }
}

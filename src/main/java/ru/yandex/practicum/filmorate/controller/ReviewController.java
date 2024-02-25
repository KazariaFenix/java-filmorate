package ru.yandex.practicum.filmorate.controller;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.aspect.Loggable;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @Loggable
    @Timed
    public Review createReview(@Valid @RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @PutMapping
    @Loggable
    @Timed
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{reviewId}")
    @Loggable
    @Timed
    public void deleteReviewById(@PathVariable("reviewId") int reviewId) {
        reviewService.deleteReviewById(reviewId);
    }

    @GetMapping("/{reviewId}")
    @Loggable
    @Timed
    public Review getReviewById(@PathVariable int reviewId) {
        return reviewService.getReviewById(reviewId);
    }

    @GetMapping
    @Loggable
    @Timed
    public List<Review> getAllReviewByFilmId(
            @RequestParam(required = false) Integer filmId,
            @RequestParam(required = false) Integer count) {
        return reviewService.getReview(filmId, count);
    }

    @PutMapping("{reviewId}/like/{userId}")
    @Loggable
    @Timed
    public void likeReview(@PathVariable int reviewId, @PathVariable int userId) {
        reviewService.likeReview(reviewId, userId);
    }

    @PutMapping("{reviewId}/dislike/{userId}")
    @Loggable
    public void dislikeReview(@PathVariable int reviewId, @PathVariable int userId) {
        reviewService.dislikeReview(reviewId, userId);
    }

    @DeleteMapping("{reviewId}/like/{userId}")
    @Loggable
    public void deleteLikeReview(@PathVariable int reviewId, @PathVariable int userId) {
        reviewService.deleteLikeReview(reviewId, userId);
    }

    @DeleteMapping("{reviewId}/dislike/{userId}")
    @Loggable
    public void deleteDislikeReview(@PathVariable int reviewId, @PathVariable int userId) {
        reviewService.deleteDislikeReview(reviewId, userId);
    }
}

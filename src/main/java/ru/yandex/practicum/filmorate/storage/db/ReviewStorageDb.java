package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ValidationException;
import java.util.List;

@Primary
@Repository
@RequiredArgsConstructor
public class ReviewStorageDb implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public Review createReview(Review review) {
        if (review.getUserId() < 0 || review.getFilmId() < 0) {
            throw new NoSuchElementException("Проверьте id фильма или id пользователя");
        }
        try {
            filmStorage.findFilmById(review.getFilmId());
            userStorage.findUserById(review.getUserId());
        } catch (NoSuchElementException e) {
            throw new ValidationException(e.getMessage());
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        int key = simpleJdbcInsert.executeAndReturnKey(review.toMap()).intValue();

        return getReviewById(key);
    }

    @Override
    public Review getReviewById(int reviewId) {
        if (validationReview(reviewId)) {
            return jdbcTemplate.queryForObject("SELECT * FROM reviews WHERE review_id = ?", (resultSet, rowNum) ->
                    Review.builder()
                            .reviewId(resultSet.getInt("review_id"))
                            .content(resultSet.getString("content"))
                            .isPositive(resultSet.getBoolean("is_positive"))
                            .userId(resultSet.getInt("user_id"))
                            .filmId(resultSet.getInt("film_id"))
                            .useful(resultSet.getInt("useful"))
                            .build(), reviewId);
        } else {
            throw new NoSuchElementException("Проверьте id отзыва");
        }
    }

    @Override
    public List<Review> getReview(Integer count) {
        return jdbcTemplate.query("SELECT * FROM reviews ORDER BY useful DESC LIMIT ?", (resultSet, rowNum) ->
                Review.builder()
                        .reviewId(resultSet.getInt("review_id"))
                        .content(resultSet.getString("content"))
                        .isPositive(resultSet.getBoolean("is_positive"))
                        .userId(resultSet.getInt("user_id"))
                        .filmId(resultSet.getInt("film_id"))
                        .useful(resultSet.getInt("useful"))
                        .build(), count);
    }

    @Override
    public List<Review> getReviewByFilmId(int filmId, int count) {
        filmStorage.findFilmById(filmId);
        return jdbcTemplate.query("SELECT *\n " +
                "FROM reviews\n " +
                "WHERE film_id = ? " +
                "ORDER BY useful DESC LIMIT ?", (resultSet, rowNum) ->
                Review.builder()
                        .reviewId(resultSet.getInt("review_id"))
                        .content(resultSet.getString("content"))
                        .isPositive(resultSet.getBoolean("is_positive"))
                        .userId(resultSet.getInt("user_id"))
                        .filmId(resultSet.getInt("film_id"))
                        .useful(resultSet.getInt("useful"))
                        .build(), filmId, count);
    }

    @Override
    public Review updateReview(Review review) {
        if (validationReview(review.getReviewId())) {
            jdbcTemplate.update("UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?",
                    review.getContent(),
                    review.getIsPositive(), review.getReviewId());
            return getReviewById(review.getReviewId());
        } else {
            throw new ValidationException("Проверьте id отзыва");
        }
    }

    @Override
    public void deleteReviewById(int reviewId) {
        if (validationReview(reviewId)) {
           // Review review = getReviewById(reviewId);
            jdbcTemplate.update("DELETE FROM reviews WHERE review_id = ?", reviewId);
        } else {
            throw new ValidationException("Проверьте id отзыва");
        }

    }

    @Override
    public void likeReview(int reviewId, int userId) {
        try {
            userStorage.findUserById(userId);
        } catch (NoSuchElementException e) {
            throw new ValidationException(e.getMessage());
        }
        if (!validationReview(reviewId)) {
            throw new ValidationException("Проверьте id отзыва");
        } else {
            jdbcTemplate.update("INSERT INTO reviews_users (user_id,review_id) VALUES (?,?)", userId, reviewId);

            jdbcTemplate.update("UPDATE reviews SET useful = useful + 1 WHERE review_id = ?", reviewId);
        }

    }

    @Override
    public void dislikeReview(int reviewId, int userId) {
        try {
            userStorage.findUserById(userId);
        } catch (NoSuchElementException e) {
            throw new ValidationException(e.getMessage());
        }
        if (!validationReview(reviewId)) {
            throw new ValidationException("Проверьте id отзыва");
        } else {
            jdbcTemplate.update("INSERT INTO reviews_users (user_id,review_id) VALUES (?,?)", userId, reviewId);

            jdbcTemplate.update("UPDATE reviews SET useful = useful - 1 WHERE review_id = ?", reviewId);
        }

    }

    @Override
    public void deleteLikeReview(int reviewId, int userId) {
        try {
            userStorage.findUserById(userId);
        } catch (NoSuchElementException e) {
            throw new ValidationException(e.getMessage());
        }
        if (!validationReview(reviewId)) {
            throw new ValidationException("Проверьте id отзыва");
        } else {
            int del = jdbcTemplate.update("DELETE FROM reviews_users " +
                    "WHERE review_id = ? AND user_id = ?", reviewId, userId);
            if (del == 1) {
                jdbcTemplate.update("UPDATE reviews SET useful = useful - 1 WHERE review_id = ?", reviewId);
            }

        }
    }

    @Override
    public void deleteDislikeReview(int reviewId, int userId) {
        try {
            userStorage.findUserById(userId);
        } catch (NoSuchElementException e) {
            throw new ValidationException(e.getMessage());
        }
        if (!validationReview(reviewId)) {
            throw new ValidationException("Проверьте id отзыва");
        } else {
            int del = jdbcTemplate.update("DELETE FROM reviews_users " +
                    "WHERE review_id = ? AND user_id = ?", reviewId, userId);
            if (del == 1) {
                jdbcTemplate.update("UPDATE reviews SET useful = useful + 1 WHERE review_id = ?", reviewId);
            }
        }
    }

    private boolean validationReview(int id) {
        SqlRowSet sqlUser = jdbcTemplate.queryForRowSet("SELECT * FROM reviews WHERE review_id = ?", id);
        return sqlUser.next();
    }
}

package ru.yandex.practicum.filmorate.usertest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmServiceTest {
    @Value(value = "${local.server.port}")
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    private final String path = "/films";

    @Test
    public void getFilmById() {
        Film film = Film.builder()
                .id(0)
                .name("Naprolom")
                .description("Vse kruto")
                .releaseDate(LocalDate.of(2015, 11, 5))
                .duration(100)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        Film other = Film.builder()
                .id(436346320)
                .name("VVlastelin Kolec")
                .description("Vse ochen kruto")
                .releaseDate(LocalDate.of(2000, 11, 5))
                .duration(500)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        final ResponseEntity<Film> responseFilm = restTemplate.postForEntity(path, new HttpEntity<>(film), Film.class);
        final ResponseEntity<Film> responseOther = restTemplate.postForEntity(path, new HttpEntity<>(other),
                Film.class);
        film = responseFilm.getBody();
        String uri = path + "/" + film.getId();
        final ResponseEntity<Film> getFilm = restTemplate.getForEntity(uri, Film.class);

        assertEquals(getFilm.getStatusCode(), HttpStatus.OK, "Неверный ответ сервера");
        assertEquals(getFilm.getBody(), film, "Неверная работа программы");
    }

    @Test
    public void getFilmUnknown() {
        Film film = Film.builder()
                .id(0)
                .name("Naprolom")
                .description("Vse kruto")
                .releaseDate(LocalDate.of(2015, 11, 5))
                .duration(100)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        Film other = Film.builder()
                .id(436346320)
                .name("VVlastelin Kolec")
                .description("Vse ochen kruto")
                .releaseDate(LocalDate.of(2000, 11, 5))
                .duration(500)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        final ResponseEntity<Film> responseFilm = restTemplate.postForEntity(path, new HttpEntity<>(film), Film.class);
        final ResponseEntity<Film> responseOther = restTemplate.postForEntity(path, new HttpEntity<>(other),
                Film.class);
        String uri = path + 426236236;
        final ResponseEntity<Film> getFilm = restTemplate.getForEntity(uri, Film.class);

        assertEquals(getFilm.getStatusCode(), HttpStatus.NOT_FOUND, "Неверный ответ сервера");
    }

    @Test
    public void getPopularFilmList() {
        Film film = Film.builder()
                .id(0)
                .name("Naprolom")
                .description("Vse kruto")
                .releaseDate(LocalDate.of(2015, 11, 5))
                .duration(100)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        Film other = Film.builder()
                .id(436346320)
                .name("VVlastelin Kolec")
                .description("Vse ochen kruto")
                .releaseDate(LocalDate.of(2000, 11, 5))
                .duration(500)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        final ResponseEntity<Film> responseFilm = restTemplate.postForEntity(path, new HttpEntity<>(film), Film.class);
        final ResponseEntity<Film> responseOther = restTemplate.postForEntity(path, new HttpEntity<>(other),
                Film.class);
        String uri = path + "/popular";
        final ResponseEntity<List> getPopularList = restTemplate.getForEntity(uri, List.class);

        assertEquals(getPopularList.getStatusCode(), HttpStatus.OK, "Неверный ответ сервера");
        assertEquals(getPopularList.getBody().size(), 2);
    }

    @Test
    public void getPopularFilm() {
        Film film = Film.builder()
                .id(0)
                .name("Naprolom")
                .description("Vse kruto")
                .releaseDate(LocalDate.of(2015, 11, 5))
                .duration(100)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        Film other = Film.builder()
                .id(436346320)
                .name("VVlastelin Kolec")
                .description("Vse ochen kruto")
                .releaseDate(LocalDate.of(2000, 11, 5))
                .duration(500)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        final ResponseEntity<Film> responseFilm = restTemplate.postForEntity(path, new HttpEntity<>(film), Film.class);
        final ResponseEntity<Film> responseOther = restTemplate.postForEntity(path, new HttpEntity<>(other),
                Film.class);
        String uri = path + "/popular?count=1";
        final ResponseEntity<List> getPopularList = restTemplate.getForEntity(uri, List.class);

        assertEquals(getPopularList.getStatusCode(), HttpStatus.OK, "Неверный ответ сервера");
        assertEquals(getPopularList.getBody().size(), 1);
    }

    @Test
    public void addLikeNormal() {
        Film film = Film.builder()
                .id(0)
                .name("Naprolom")
                .description("Vse kruto")
                .releaseDate(LocalDate.of(2015, 11, 5))
                .duration(100)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        Film other = Film.builder()
                .id(436346320)
                .name("VVlastelin Kolec")
                .description("Vse ochen kruto")
                .releaseDate(LocalDate.of(2000, 11, 5))
                .duration(500)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        final ResponseEntity<Film> responseFilm = restTemplate.postForEntity(path, new HttpEntity<>(film), Film.class);
        final ResponseEntity<Film> responseOther = restTemplate.postForEntity(path, new HttpEntity<>(other),
                Film.class);
        User user = User.builder()
                .id(0)
                .email("yandex@yandex.ru")
                .name("Valerii Programist")
                .login("VlP").birthday(LocalDate.of(1956, 11, 1))
                .build();
        final ResponseEntity<User> responseUser = restTemplate.postForEntity("/users",
                new HttpEntity<>(user), User.class);
        film = responseFilm.getBody();
        user = responseUser.getBody();
        String uri = path + "/" + film.getId() + "/like/" + user.getId();
        final ResponseEntity<Void> responseLike = restTemplate.exchange(uri, HttpMethod.PUT, HttpEntity.EMPTY,
                Void.class);

        assertEquals(HttpStatus.OK, responseLike.getStatusCode(), "Неверный ответ сервера");
    }

    @Test
    public void addLikeRepeat() {
        Film film = Film.builder()
                .id(0)
                .name("Naprolom")
                .description("Vse kruto")
                .releaseDate(LocalDate.of(2015, 11, 5))
                .duration(100)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        Film other = Film.builder()
                .id(436346320)
                .name("VVlastelin Kolec")
                .description("Vse ochen kruto")
                .releaseDate(LocalDate.of(2000, 11, 5))
                .duration(500)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        final ResponseEntity<Film> responseFilm = restTemplate.postForEntity(path, new HttpEntity<>(film), Film.class);
        final ResponseEntity<Film> responseOther = restTemplate.postForEntity(path, new HttpEntity<>(other),
                Film.class);
        User user = User.builder()
                .id(0)
                .email("yandex@yandex.ru")
                .name("Valerii Programist")
                .login("VlP").birthday(LocalDate.of(1956, 11, 1))
                .build();
        final ResponseEntity<User> responseUser = restTemplate.postForEntity("/users",
                new HttpEntity<>(user), User.class);
        film = responseFilm.getBody();
        user = responseUser.getBody();
        String uri = path + "/" + film.getId() + "/like/" + user.getId();
        final ResponseEntity<Void> responseLike = restTemplate.exchange(uri, HttpMethod.PUT, HttpEntity.EMPTY,
                Void.class);
        final ResponseEntity<Void> responseLikeTwo = restTemplate.exchange(uri, HttpMethod.PUT, HttpEntity.EMPTY,
                Void.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseLikeTwo.getStatusCode(),
                "Неверный ответ сервера");
    }

    @Test
    public void addLikeUnknown() {
        Film film = Film.builder()
                .id(0)
                .name("Naprolom")
                .description("Vse kruto")
                .releaseDate(LocalDate.of(2015, 11, 5))
                .duration(100)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        Film other = Film.builder()
                .id(436346320)
                .name("VVlastelin Kolec")
                .description("Vse ochen kruto")
                .releaseDate(LocalDate.of(2000, 11, 5))
                .duration(500)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        final ResponseEntity<Film> responseFilm = restTemplate.postForEntity(path, new HttpEntity<>(film), Film.class);
        final ResponseEntity<Film> responseOther = restTemplate.postForEntity(path, new HttpEntity<>(other),
                Film.class);
        User user = User.builder()
                .id(0)
                .email("yandex@yandex.ru")
                .name("Valerii Programist")
                .login("VlP").birthday(LocalDate.of(1956, 11, 1))
                .build();
        final ResponseEntity<User> responseUser = restTemplate.postForEntity("/users",
                new HttpEntity<>(user), User.class);
        user = responseUser.getBody();
        String uri = path + "/" + 46946 + "/like/" + user.getId();
        final ResponseEntity<Void> responseLike = restTemplate.exchange(uri, HttpMethod.PUT, HttpEntity.EMPTY,
                Void.class);

        assertEquals(HttpStatus.NOT_FOUND, responseLike.getStatusCode(), "Неверный ответ сервера");
    }

    @Test
    public void deleteLikeNormal() {
        Film film = Film.builder()
                .id(0)
                .name("Naprolom")
                .description("Vse kruto")
                .releaseDate(LocalDate.of(2015, 11, 5))
                .duration(100)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        Film other = Film.builder()
                .id(436346320)
                .name("VVlastelin Kolec")
                .description("Vse ochen kruto")
                .releaseDate(LocalDate.of(2000, 11, 5))
                .duration(500)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        final ResponseEntity<Film> responseFilm = restTemplate.postForEntity(path, new HttpEntity<>(film), Film.class);
        final ResponseEntity<Film> responseOther = restTemplate.postForEntity(path, new HttpEntity<>(other),
                Film.class);
        User user = User.builder()
                .id(0)
                .email("yandex@yandex.ru")
                .name("Valerii Programist")
                .login("VlP").birthday(LocalDate.of(1956, 11, 1))
                .build();
        final ResponseEntity<User> responseUser = restTemplate.postForEntity("/users",
                new HttpEntity<>(user), User.class);
        film = responseFilm.getBody();
        user = responseUser.getBody();
        String uri = path + "/" + film.getId() + "/like/" + user.getId();
        final ResponseEntity<Void> responseLike = restTemplate.exchange(uri, HttpMethod.PUT, HttpEntity.EMPTY,
                Void.class);
        final ResponseEntity<Void> responseLikeDel = restTemplate.exchange(uri, HttpMethod.DELETE, HttpEntity.EMPTY,
                Void.class);

        assertEquals(HttpStatus.OK, responseLikeDel.getStatusCode(), "Неверный ответ сервера");
    }

    @Test
    public void deleteLikeRepeat() {
        Film film = Film.builder()
                .id(0)
                .name("Naprolom")
                .description("Vse kruto")
                .releaseDate(LocalDate.of(2015, 11, 5))
                .duration(100)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        Film other = Film.builder()
                .id(436346320)
                .name("VVlastelin Kolec")
                .description("Vse ochen kruto")
                .releaseDate(LocalDate.of(2000, 11, 5))
                .duration(500)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        final ResponseEntity<Film> responseFilm = restTemplate.postForEntity(path, new HttpEntity<>(film), Film.class);
        final ResponseEntity<Film> responseOther = restTemplate.postForEntity(path, new HttpEntity<>(other),
                Film.class);
        User user = User.builder()
                .id(0)
                .email("yandex@yandex.ru")
                .name("Valerii Programist")
                .login("VlP").birthday(LocalDate.of(1956, 11, 1))
                .build();
        final ResponseEntity<User> responseUser = restTemplate.postForEntity("/users",
                new HttpEntity<>(user), User.class);
        film = responseFilm.getBody();
        user = responseUser.getBody();
        String uri = path + "/" + film.getId() + "/like/" + user.getId();
        final ResponseEntity<Void> responseLike = restTemplate.exchange(uri, HttpMethod.PUT, HttpEntity.EMPTY,
                Void.class);
        final ResponseEntity<Void> responseLikeDel = restTemplate.exchange(uri, HttpMethod.DELETE, HttpEntity.EMPTY,
                Void.class);
        final ResponseEntity<Void> responseLikeDelTwo = restTemplate.exchange(uri, HttpMethod.DELETE, HttpEntity.EMPTY,
                Void.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseLikeDelTwo.getStatusCode(),
                "Неверный ответ сервера");
    }

    @Test
    public void deleteLikeUnknown() {
        Film film = Film.builder()
                .id(0)
                .name("Naprolom")
                .description("Vse kruto")
                .releaseDate(LocalDate.of(2015, 11, 5))
                .duration(100)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        Film other = Film.builder()
                .id(436346320)
                .name("VVlastelin Kolec")
                .description("Vse ochen kruto")
                .releaseDate(LocalDate.of(2000, 11, 5))
                .duration(500)
                .rate(0)
                .userLike(new ArrayList<>())
                .build();
        final ResponseEntity<Film> responseFilm = restTemplate.postForEntity(path, new HttpEntity<>(film), Film.class);
        final ResponseEntity<Film> responseOther = restTemplate.postForEntity(path, new HttpEntity<>(other),
                Film.class);
        User user = User.builder()
                .id(0)
                .email("yandex@yandex.ru")
                .name("Valerii Programist")
                .login("VlP").birthday(LocalDate.of(1956, 11, 1))
                .build();
        final ResponseEntity<User> responseUser = restTemplate.postForEntity("/users",
                new HttpEntity<>(user), User.class);
        film = responseFilm.getBody();
        user = responseUser.getBody();
        String uri = path + "/" + film.getId() + "/like/" + user.getId();
        final ResponseEntity<Void> responseLike = restTemplate.exchange(uri, HttpMethod.PUT, HttpEntity.EMPTY,
                Void.class);
        uri = path + "/" + 23235326 + "/like/" + user.getId();
        final ResponseEntity<Void> responseLikeDel = restTemplate.exchange(uri, HttpMethod.DELETE, HttpEntity.EMPTY,
                Void.class);

        assertEquals(HttpStatus.NOT_FOUND, responseLikeDel.getStatusCode(), "Неверный ответ сервера");
    }
}

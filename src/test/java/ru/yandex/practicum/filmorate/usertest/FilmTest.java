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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmTest {
    @Value(value = "${local.server.port}")
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    private final String path = "/films";

    @Test
    public void postFilmNormal() {
        Film film = Film.builder().id(0).name("Naprolom").description("Vse kruto")
                .releaseDate(LocalDate.of(2015, 11, 5)).duration(100)
                .rate(0).userLike(new ArrayList<>()).build();
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        film = film.toBuilder().id(1).build();
        assertEquals(response.getBody(), film, "Неверная выдача id");
    }

    @Test
    public void postFilmBlankName() {
        Film film = Film.builder().id(0).name(" ").description("Vse kruto")
                .releaseDate(LocalDate.of(2015, 11, 5)).duration(100)
                .rate(0).userLike(new ArrayList<>()).build();
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 0, "Неверная работа программы");
        assertEquals(response.getStatusCodeValue(), 500, "Неверная выдача id");
    }

    @Test
    public void postFilmNullName() {
        Film film = Film.builder().id(0).description("Vse kruto")
                .releaseDate(LocalDate.of(2015, 11, 5)).duration(100)
                .rate(0).userLike(new ArrayList<>()).build();
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 0, "Неверная работа программы");
        assertEquals(response.getStatusCodeValue(), 500, "Неверная работа сервера");
    }

    @Test
    public void postFilmOverMaxDesc() {
        Film film = Film.builder().id(0).name("Naprolom")
                .releaseDate(LocalDate.of(2015, 11, 5)).duration(100)
                .rate(0).userLike(new ArrayList<>()).build();
        String desc = "";
        final int maxDesc = 201;
        for (int i = 0; i < maxDesc; i++) {
            char letter = 'a';
            desc += letter;
        }
        film = film.toBuilder().description(desc).build();
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 0, "Неверная работа программы");
        assertEquals(response.getStatusCodeValue(), 500, "Неверный ответ сервера");
    }

    @Test
    public void postFilmFailDateReleasePast() {
        Film film = Film.builder().id(0).name("Naprolom").description("Vse kruto")
                .releaseDate(LocalDate.of(1895, 12, 28)).duration(100)
                .rate(0).userLike(new ArrayList<>()).build();
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 0, "Неверная работа программы");
        assertEquals(response.getStatusCodeValue(), 500, "Неверная работа сервера");
    }

    @Test
    public void postFilmDateReleaseFuture() {
        Film film = Film.builder().id(0).name("Naprolom").description("Vse kruto")
                .releaseDate(LocalDate.of(3015, 11, 5)).duration(100)
                .rate(0).userLike(new ArrayList<>()).build();
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        film = film.toBuilder().id(1).build();
        assertEquals(response.getBody(), film, "Неверная выдача id");
    }

    @Test
    public void postFilmFailDurationNegative() {
        Film film = Film.builder().id(0).name("Naprolom").description("Vse kruto")
                .releaseDate(LocalDate.of(2015, 11, 5)).duration(-100)
                .rate(0).userLike(new ArrayList<>()).build();
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 0, "Неверная работа программы");
        assertEquals(response.getStatusCodeValue(), 500, "Неверная работа сервера");
    }

    @Test
    public void postFilmFailDurationZero() {
        Film film = Film.builder().id(0).name("Naprolom").description("Vse kruto")
                .releaseDate(LocalDate.of(2015, 11, 5)).duration(0)
                .rate(0).userLike(new ArrayList<>()).build();
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 0, "Неверная работа программы");
        assertEquals(response.getStatusCodeValue(), 500, "Неверная работа сервера");
    }

    @Test
    public void putFilmNormal() {
        Film film = Film.builder().id(0).name("Naprolom").description("Vse kruto")
                .releaseDate(LocalDate.of(2015, 11, 5)).duration(100)
                .rate(0).userLike(new ArrayList<>()).build();
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final Film putFilm = Film.builder().id(1).name("VVlastelin Kolec").description("Vse ochen kruto")
                .releaseDate(LocalDate.of(2000, 11, 5)).duration(500)
                .rate(0).userLike(new ArrayList<>()).build();
        final HttpEntity<Film> newRequest = new HttpEntity<>(putFilm);
        final ResponseEntity<Film> newResponse = restTemplate.exchange(path, HttpMethod.PUT, newRequest, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        assertEquals(newResponse.getBody(), putFilm, "Неверная работа программы");
    }

    @Test
    public void putFilmUnknown() {
        Film film = Film.builder().id(0).name("Naprolom").description("Vse kruto")
                .releaseDate(LocalDate.of(2015, 11, 5)).duration(100).
                rate(0).userLike(new ArrayList<>()).build();
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final Film putFilm = Film.builder().id(436346320).name("VVlastelin Kolec").description("Vse ochen kruto")
                .releaseDate(LocalDate.of(2000, 11, 5)).duration(500)
                .rate(0).userLike(new ArrayList<>()).build();
        final HttpEntity<Film> newRequest = new HttpEntity<>(putFilm);
        final ResponseEntity<Film> newResponse = restTemplate.exchange(path, HttpMethod.PUT, newRequest, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        assertEquals(newResponse.getStatusCodeValue(), 500, "Неверная работа программы");
    }

    @Test
    public void getFilmList() {
        Film film = Film.builder().id(0).name("Naprolom").description("Vse kruto")
                .releaseDate(LocalDate.of(2015, 11, 5)).duration(100).
                rate(0).userLike(new ArrayList<>()).build();
        Film other = Film.builder().id(436346320).name("VVlastelin Kolec").description("Vse ochen kruto")
                .releaseDate(LocalDate.of(2000, 11, 5)).duration(500)
                .rate(0).userLike(new ArrayList<>()).build();
        final ResponseEntity<Film> responseFilm = restTemplate.postForEntity(path, new HttpEntity<>(film), Film.class);
        final ResponseEntity<Film> responseOther = restTemplate.postForEntity(path, new HttpEntity<>(other),
                Film.class);
        final ResponseEntity<List> getFilm = restTemplate.getForEntity(path, List.class);

        assertEquals(HttpStatus.OK, getFilm.getStatusCode(), "Неверный ответ сервера");
        assertEquals(getFilm.getBody().size(), 2, "Неверная работа программы");
    }

    @Test
    public void postFilmNull() {
        final Film film = null;
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(response.getStatusCodeValue(), 500, "Неверная ответ сервера");
        assertEquals(getResponse.getBody().size(), 0, "Неверная работа программы");
    }
}

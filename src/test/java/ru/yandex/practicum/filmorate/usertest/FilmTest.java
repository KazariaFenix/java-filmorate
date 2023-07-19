package ru.yandex.practicum.filmorate.usertest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;

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
        final Film film = new Film(0, "Naprolom", "Vse kruto",
                LocalDate.of(2015, 11, 5), 100);
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        film.setId(1);
        assertEquals(response.getBody(), film, "Неверная выдача id");
    }

    @Test
    public void postFilmBlankName() {
        final Film film = new Film(0, "", "Vse kruto",
                LocalDate.of(2015, 11, 5), 100);
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 0, "Неверная работа программы");
        assertEquals(response.getStatusCodeValue(), 400, "Неверная выдача id");
    }

    @Test
    public void postFilmNullName() {
        final Film film = new Film(0, null, "Vse kruto",
                LocalDate.of(2015, 11, 5), 100);
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 0, "Неверная работа программы");
        assertEquals(response.getStatusCodeValue(), 400, "Неверная работа сервера");
    }

    @Test
    public void postFilmOverMaxDesc() {
        final Film film = new Film(0, "Naprolom", "Vse kruto",
                LocalDate.of(2015, 11, 5), 100);
        String desc = "";
        final int maxDesc = 201;
        for (int i = 0; i < maxDesc; i++) {
            char letter = 'a';
            desc += letter;
        }
        film.setDescription(desc);
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 0, "Неверная работа программы");
        assertEquals(response.getStatusCodeValue(), 400, "Неверный ответ сервера");
    }

    @Test
    public void postFilmFailDateReleasePast() {
        final Film film = new Film(0, "Naprolom", "Vse kruto",
                LocalDate.of(1895, 12, 28), 100);
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 0, "Неверная работа программы");
        assertEquals(response.getStatusCodeValue(), 400, "Неверная работа сервера");
    }

    @Test
    public void postFilmDateReleaseFuture() {
        final Film film = new Film(0, "Naprolom", "Vse kruto",
                LocalDate.of(3015, 11, 5), 100);
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        film.setId(1);
        assertEquals(response.getBody(), film, "Неверная выдача id");
    }

    @Test
    public void postFilmFailDurationNegative() {
        final Film film = new Film(0, "Naprolom", "Vse kruto",
                LocalDate.of(1995, 12, 28), -100);
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 0, "Неверная работа программы");
        assertEquals(response.getStatusCodeValue(), 400, "Неверная работа сервера");
    }

    @Test
    public void postFilmFailDurationZero() {
        final Film film = new Film(0, "Naprolom", "Vse kruto",
                LocalDate.of(1995, 12, 28), 0);
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 0, "Неверная работа программы");
        assertEquals(response.getStatusCodeValue(), 400, "Неверная работа сервера");
    }

    @Test
    public void putFilmNormal() {
        final Film film = new Film(0, "Naprolom", "Vse kruto",
                LocalDate.of(2015, 11, 5), 100);
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final Film putFilm = new Film(1, "7 element", "Esche kruche",
                LocalDate.of(2000, 11, 5), 150);
        final HttpEntity<Film> newRequest = new HttpEntity<>(putFilm);
        final ResponseEntity<Film> newResponse = restTemplate.exchange(path, HttpMethod.PUT, newRequest, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        assertEquals(newResponse.getBody(), putFilm, "Неверная работа программы");
    }

    @Test
    public void putFilmUnknown() {
        final Film film = new Film(0, "Naprolom", "Vse kruto",
                LocalDate.of(2015, 11, 5), 100);
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final Film putFilm = new Film(23523623, "7 element", "Esche kruche",
                LocalDate.of(2000, 11, 5), 150);
        final HttpEntity<Film> newRequest = new HttpEntity<>(putFilm);
        final ResponseEntity<Film> newResponse = restTemplate.exchange(path, HttpMethod.PUT, newRequest, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        assertEquals(newResponse.getStatusCodeValue(), 500, "Неверная работа программы");
    }

    @Test
    public void postFilmNull() {
        final Film film = null;
        final HttpEntity<Film> request = new HttpEntity<>(film);
        final ResponseEntity<Film> response = restTemplate.postForEntity(path, request, Film.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);

        assertEquals(response.getStatusCodeValue(), 415, "Неверная ответ сервера");
        assertEquals(getResponse.getBody().size(), 0, "Неверная работа программы");
    }
}

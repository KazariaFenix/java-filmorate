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
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTest {
    @Value(value = "${local.server.port}")
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    private final String path = "/users";

    @Test
    void postUserNormal() throws IOException, InterruptedException {
        final User user = new User(0, "yandex@yandex.ru", "Valerii Programist", "VlP",
                LocalDate.of(1956, 11, 1));
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final User newUser = response.getBody();
        user.setId(1);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        assertEquals(newUser, user);
    }

    @Test
    void postUserBlankEmail() throws IOException, InterruptedException {
        final User user = new User(0, "", "Valerii Programist", "VlP",
                LocalDate.of(1956, 11, 1));
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final int statusCode = response.getStatusCodeValue();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 0, "Добавление Юзера с неподходящим Email");
        assertEquals(statusCode, 400);
    }

    @Test
    void postUserNullEmail() throws IOException, InterruptedException {
        final User user = new User(0, null, "Valerii Programist", "VlP",
                LocalDate.of(1956, 11, 1));
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final int statusCode = response.getStatusCodeValue();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 0, "Добавление Юзера с неподходящим Email");
        assertEquals(statusCode, 400);
    }

    @Test
    void postUserFailEmail() throws IOException, InterruptedException {
        final User user = new User(0, "dhdfnfnf", "Valerii Programist", "VlP",
                LocalDate.of(1956, 11, 1));
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final int statusCode = response.getStatusCodeValue();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 0, "Добавление Юзера с неподходящим Email");
        assertEquals(statusCode, 400);
    }

    @Test
    void postUserBlankLogin() throws IOException, InterruptedException {
        final User user = new User(0, "yandex@yandex.ru", "Valerii Programist", " ",
                LocalDate.of(1956, 11, 1));
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final int statusCode = response.getStatusCodeValue();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 0, "Добавление Юзера с неподходящим Login");
        assertEquals(statusCode, 400);
    }

    @Test
    void postUserNullLogin() throws IOException, InterruptedException {
        final User user = new User(0, "yandex@yandex.ru", "Valerii Programist", null,
                LocalDate.of(1956, 11, 1));
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final int statusCode = response.getStatusCodeValue();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 0, "Добавление Юзера с неподходящим Login");
        assertEquals(statusCode, 400);
    }

    @Test
    void postUserFailBirthDayFuture() throws IOException, InterruptedException {
        final User user = new User(0, "yandex@yandex.ru", "Valerii Programist", "VlP",
                LocalDate.of(2856, 11, 1));
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final int statusCode = response.getStatusCodeValue();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 0, "Добавление Юзера с неподходящим Birthday");
        assertEquals(statusCode, 400);
    }

    @Test
    void postUserFailBirthDayPresent() throws IOException, InterruptedException {
        final User user = new User(0, "yandex@yandex.ru", "Valerii Programist", "VlP",
                LocalDate.now());
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final int statusCode = response.getStatusCodeValue();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 0, "Добавление Юзера с неподходящим Birthday");
        assertEquals(statusCode, 400);
    }

    @Test
    void postUserFailBirthDayPast() throws IOException, InterruptedException {
        final User user = new User(0, "yandex@yandex.ru", "Valerii Programist", "VlP",
                LocalDate.of(1900, 1, 1));
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final int statusCode = response.getStatusCodeValue();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 0, "Добавление Юзера с неподходящим Birthday");
        assertEquals(statusCode, 400);
    }

    @Test
    void postUserBoudaryCasePast() throws IOException, InterruptedException {
        final User user = new User(0, "yandex@yandex.ru", "VlP", "VlP",
                LocalDate.of(1900, 1, 2));
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final User newUser = response.getBody();
        user.setId(1);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        assertEquals(newUser, user, "Неверное добавление пользователя");
    }

    @Test
    void postUserBoudaryCasePresent() throws IOException, InterruptedException {
        final User user = new User(0, "yandex@yandex.ru", "VlP", "VlP",
                LocalDate.now().minusDays(1));
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final User newUser = response.getBody();
        user.setId(1);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        assertEquals(newUser, user, "Неверное добавление пользователя");
    }

    @Test
    void postUserNullName() throws IOException, InterruptedException {
        final User user = new User(0, "yandex@yandex.ru", null, "VlP",
                LocalDate.of(1956, 11, 1));
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final User newUser = response.getBody();
        user.setId(1);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        assertEquals(newUser.getName(), newUser.getLogin(), "Неверная замена null имени");
    }

    @Test
    void postUserBlankName() throws IOException, InterruptedException {
        final User user = new User(0, "yandex@yandex.ru", " ", "VlP",
                LocalDate.of(1956, 11, 1));
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final User newUser = response.getBody();
        user.setId(1);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        assertEquals(newUser.getName(), newUser.getLogin(), "Неверная замена пустого имени");
    }

    @Test
    void putUserNormal() {
        final User user = new User(0, "yandex@yandex.ru", "Valerii Programist", "VlP",
                LocalDate.of(1956, 11, 1));
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final User putUser = new User(1, "yandex@yandex.ru", "Alexandro Designer", "Qwerty",
                LocalDate.of(1976, 11, 1));
        final HttpEntity<User> newRequest = new HttpEntity<>(putUser);
        final ResponseEntity<User> newResponse = restTemplate.exchange(path, HttpMethod.PUT, newRequest, User.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        assertEquals(newResponse.getBody(), putUser, "Неверная работа программы");
    }

    @Test
    void putUserUnknown() {
        final User user = new User(0, "yandex@yandex.ru", "Valerii Programist", "VlP",
                LocalDate.of(1956, 11, 1));
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final User putUser = new User(46364634, "yandex@yandex.ru", "Alexandro Designer", "Qwerty",
                LocalDate.of(1976, 11, 1));
        final HttpEntity<User> newRequest = new HttpEntity<>(putUser);
        final ResponseEntity<User> newResponse = restTemplate.exchange(path, HttpMethod.PUT, newRequest, User.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        assertEquals(newResponse.getStatusCodeValue(), 500, "Неверная работа программы");
    }

    @Test
    public void postUserNull() {
        final User user = null;
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 0, "Неверная работа программы");
        assertEquals(response.getStatusCodeValue(), 415, "Неверный ответ сервера");
    }
}

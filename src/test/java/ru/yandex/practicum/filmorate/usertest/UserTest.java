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
import java.util.*;

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
        User user = User.builder().id(0).email("yandex@yandex.ru").name("Valerii Programist")
                .login("VlP").birthday(LocalDate.of(1956, 11, 1)).build();
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final User newUser = response.getBody();
        user = user.toBuilder().id(1).build();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        assertEquals(newUser, user);
    }

    @Test
    void postUserBlankEmail() throws IOException, InterruptedException {
        User user = User.builder().id(0).email(" ").name("Valerii Programist")
                .login("VlP").birthday(LocalDate.of(1956, 11, 1)).build();
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final int statusCode = response.getStatusCodeValue();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 0, "Добавление Юзера с неподходящим Email");
        assertEquals(statusCode, 500);
    }

    @Test
    void postUserNullEmail() throws IOException, InterruptedException {
        User user = User.builder().id(0).name("Valerii Programist")
                .login("VlP").birthday(LocalDate.of(1956, 11, 1)).build();
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final int statusCode = response.getStatusCodeValue();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 0, "Добавление Юзера с неподходящим Email");
        assertEquals(statusCode, 500);
    }

    @Test
    void postUserFailEmail() throws IOException, InterruptedException {
        User user = User.builder().id(0).email("fdhnndndn").name("Valerii Programist")
                .login("VlP").birthday(LocalDate.of(1956, 11, 1)).build();
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final int statusCode = response.getStatusCodeValue();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 0, "Добавление Юзера с неподходящим Email");
        assertEquals(statusCode, 500);
    }

    @Test
    void postUserBlankLogin() throws IOException, InterruptedException {
        User user = User.builder().id(0).email("yandex@yandex.ru").name("Valerii Programist").login(" ")
                .birthday(LocalDate.of(1956, 11, 1)).build();
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final int statusCode = response.getStatusCodeValue();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 0, "Добавление Юзера с неподходящим Login");
        assertEquals(statusCode, 500);
    }

    @Test
    void postUserNullLogin() throws IOException, InterruptedException {
        User user = User.builder().id(0).email("yandex@yandex.ru").name("Valerii Programist")
                .birthday(LocalDate.of(1956, 11, 1)).build();
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final int statusCode = response.getStatusCodeValue();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 0, "Добавление Юзера с неподходящим Login");
        assertEquals(statusCode, 500);
    }

    @Test
    void postUserFailBirthDayFuture() throws IOException, InterruptedException {
        User user = User.builder().id(0).email("yandex@yandex.ru").name("Valerii Programist")
                .login("VlP").birthday(LocalDate.of(2956, 11, 1)).build();
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final int statusCode = response.getStatusCodeValue();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 0, "Добавление Юзера с неподходящим Birthday");
        assertEquals(statusCode, 500);
    }

    @Test
    void postUserFailBirthDayPresent() throws IOException, InterruptedException {
        User user = User.builder().id(0).email("yandex@yandex.ru").name("Valerii Programist")
                .login("VlP").birthday(LocalDate.now()).build();
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final int statusCode = response.getStatusCodeValue();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 0, "Добавление Юзера с неподходящим Birthday");
        assertEquals(statusCode, 500);
    }

    @Test
    void postUserFailBirthDayPast() throws IOException, InterruptedException {
        User user = User.builder().id(0).email("yandex@yandex.ru").name("Valerii Programist")
                .login("VlP").birthday(LocalDate.of(1900, 1, 1)).build();
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final int statusCode = response.getStatusCodeValue();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 0, "Добавление Юзера с неподходящим Birthday");
        assertEquals(statusCode, 500);
    }

    @Test
    void postUserBoudaryCasePast() throws IOException, InterruptedException {
        User user = User.builder().id(0).email("yandex@yandex.ru").name("Valerii Programist")
                .login("VlP").birthday(LocalDate.of(1900, 1, 2)).build();
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final User newUser = response.getBody();
        user = user.toBuilder().id(1).build();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        assertEquals(newUser, user, "Неверное добавление пользователя");
    }

    @Test
    void postUserBoudaryCasePresent() throws IOException, InterruptedException {
        User user = User.builder().id(0).email("yandex@yandex.ru").name("Valerii Programist")
                .login("VlP").birthday(LocalDate.now().minusDays(1)).build();
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final User newUser = response.getBody();
        user = user.toBuilder().id(1).build();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        assertEquals(newUser, user, "Неверное добавление пользователя");
    }

    @Test
    void postUserNullName() throws IOException, InterruptedException {
        User user = User.builder().id(0).email("yandex@yandex.ru")
                .login("VlP").birthday(LocalDate.of(1956, 11, 1)).build();
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final User newUser = response.getBody();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        assertEquals(newUser.getName(), newUser.getLogin(), "Неверная замена null имени");
    }

    @Test
    void postUserBlankName() throws IOException, InterruptedException {
        User user = User.builder().id(0).email("yandex@yandex.ru").name(" ")
                .login("VlP").birthday(LocalDate.of(1956, 11, 1)).build();
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final User newUser = response.getBody();
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        assertEquals(newUser.getName(), newUser.getLogin(), "Неверная замена пустого имени");
    }

    @Test
    void putUserNormal() {
        User user = User.builder().id(0).email("yandex@yandex.ru").name("Valerii Programist")
                .login("VlP").birthday(LocalDate.of(1956, 11, 1)).build();
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        User putUser = user.builder().id(1).email("yandex@yandex.ru").name("Valerii Programist")
                .login("VlP").birthday(LocalDate.of(1956, 11, 1)).build();
        final HttpEntity<User> newRequest = new HttpEntity<>(putUser);
        final ResponseEntity<User> newResponse = restTemplate.exchange(path, HttpMethod.PUT, newRequest, User.class);
        final ResponseEntity<ArrayList> getResponse = restTemplate.getForEntity(path, ArrayList.class);
        assertEquals(getResponse.getBody().size(), 1, "Неверная работа программы");
        assertEquals(newResponse.getBody(), putUser, "Неверная работа программы");
    }

    @Test
    void putUserUnknown() {
        User user = User.builder().id(0).email("yandex@yandex.ru").name("Valerii Programist")
                .login("VlP").birthday(LocalDate.of(1956, 11, 1)).build();
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        User putUser = user.builder().id(2366230).email("yandex@yandex.ru").name("Valerii Programist")
                .login("VlP").birthday(LocalDate.of(1956, 11, 1)).build();
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
        assertEquals(response.getStatusCodeValue(), 500, "Неверный ответ сервера");
    }

    @Test
    public void getEmptyList() {
        User user = User.builder().id(0).email("yandex@yandex.ru").name("Valerii Programist")
                .login("VlP").birthday(LocalDate.of(1956, 11, 1)).build();
        final HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<User> response = restTemplate.postForEntity(path, request, User.class);
        final ResponseEntity<ArrayList> getResp = restTemplate.getForEntity(path + "/1/friends", ArrayList.class);
        assertEquals(getResp.getStatusCodeValue(), 200, "Неверный ответ сервера");
        assertEquals(getResp.getBody().size(), 0, "Неверная работа программы");
    }
}

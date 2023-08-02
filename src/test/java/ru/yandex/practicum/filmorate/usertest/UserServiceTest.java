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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceTest {
    @Value(value = "${local.server.port}")
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    private final String path = "/users";

    @Test
    public void getUserById() {
        User user = User.builder()
                .id(0)
                .email("yandex@yandex.ru")
                .name("Valerii Programist")
                .login("VlP")
                .birthday(LocalDate.of(1956, 11, 1))
                .build();
        final ResponseEntity<User> response = restTemplate.postForEntity(path, new HttpEntity<>(user), User.class);
        User friend = User.builder()
                .id(0)
                .email("yandexqwqfgw@yandex.ru")
                .name("Anatolii Programist")
                .login("AlP")
                .birthday(LocalDate.of(1976, 11, 1))
                .build();
        final ResponseEntity<User> responseFriend = restTemplate.postForEntity(path, new HttpEntity<>(friend),
                User.class);
        user = response.getBody();
        final ResponseEntity<User> responseGet = restTemplate.getForEntity(path + "/" + user.getId(), User.class);

        assertEquals(responseGet.getBody(), user, "Неверная работа программы");
    }

    @Test
    public void getUserUnknown() {
        User user = User.builder()
                .id(0)
                .email("yandex@yandex.ru")
                .name("Valerii Programist")
                .login("VlP")
                .birthday(LocalDate.of(1956, 11, 1))
                .build();
        final ResponseEntity<User> response = restTemplate.postForEntity(path, new HttpEntity<>(user), User.class);
        User friend = User.builder()
                .id(0)
                .email("yandexqwqfgw@yandex.ru")
                .name("Anatolii Programist")
                .login("AlP")
                .birthday(LocalDate.of(1976, 11, 1))
                .build();
        final ResponseEntity<User> responseFriend = restTemplate.postForEntity(path, new HttpEntity<>(friend),
                User.class);
        final ResponseEntity<User> responseGet = restTemplate.getForEntity(path + "/" + 235235325, User.class);

        assertEquals(HttpStatus.NOT_FOUND, responseGet.getStatusCode(), "Неверный ответ сервера");
    }

    @Test
    public void addFriendNormal() {
        User user = User.builder()
                .id(0)
                .email("yandex@yandex.ru")
                .name("Valerii Programist")
                .login("VlP")
                .birthday(LocalDate.of(1956, 11, 1))
                .build();
        final ResponseEntity<User> response = restTemplate.postForEntity(path, new HttpEntity<>(user), User.class);
        User friend = User.builder()
                .id(0)
                .email("yandexqwqfgw@yandex.ru")
                .name("Anatolii Programist")
                .login("AlP")
                .birthday(LocalDate.of(1976, 11, 1))
                .build();
        final ResponseEntity<User> responseFriend = restTemplate.postForEntity(path, new HttpEntity<>(friend),
                User.class);
        user = response.getBody();
        friend = responseFriend.getBody();
        final String uri = path + "/" + user.getId() + "/friends" + "/" + friend.getId();
        final ResponseEntity<Void> responseFriends = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);

        assertEquals(responseFriends.getStatusCode(), HttpStatus.OK, "Неверный ответ сервера");
    }

    @Test
    public void addFriendUnknown() {
        User user = User.builder()
                .id(0)
                .email("yandex@yandex.ru")
                .name("Valerii Programist")
                .login("VlP")
                .birthday(LocalDate.of(1956, 11, 1))
                .build();
        final ResponseEntity<User> response = restTemplate.postForEntity(path, new HttpEntity<>(user), User.class);
        User friend = User.builder()
                .id(0)
                .email("yandexqwqfgw@yandex.ru")
                .name("Anatolii Programist")
                .login("AlP")
                .birthday(LocalDate.of(1976, 11, 1))
                .build();
        final ResponseEntity<User> responseFriend = restTemplate.postForEntity(path, new HttpEntity<>(friend),
                User.class);
        user = response.getBody();
        friend = responseFriend.getBody();
        final String uri = path + "/" + user.getId() + "/friends" + "/" + (friend.getId() + 1);
        final ResponseEntity<Void> responseFriends = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);

        assertEquals(responseFriends.getStatusCode(), HttpStatus.NOT_FOUND, "Неверный ответ сервера");
    }

    @Test
    public void addFriendRepeat() {
        User user = User.builder()
                .id(0)
                .email("yandex@yandex.ru")
                .name("Valerii Programist")
                .login("VlP")
                .birthday(LocalDate.of(1956, 11, 1))
                .build();
        final ResponseEntity<User> response = restTemplate.postForEntity(path, new HttpEntity<>(user), User.class);
        User friend = User.builder()
                .id(0)
                .email("yandexqwqfgw@yandex.ru")
                .name("Anatolii Programist")
                .login("AlP")
                .birthday(LocalDate.of(1976, 11, 1))
                .build();
        final ResponseEntity<User> responseFriend = restTemplate.postForEntity(path, new HttpEntity<>(friend),
                User.class);
        user = response.getBody();
        friend = responseFriend.getBody();
        final String uri = path + "/" + user.getId() + "/friends" + "/" + friend.getId();
        final ResponseEntity<Void> responseFriends = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        final ResponseEntity<Void> responseFriendsRepeat = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);

        assertEquals(responseFriendsRepeat.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR,
                "Неверный ответ сервера");
    }

    @Test
    public void getFriends() {
        User user = User.builder()
                .id(0)
                .email("yandex@yandex.ru")
                .name("Valerii Programist")
                .login("VlP")
                .birthday(LocalDate.of(1956, 11, 1))
                .build();
        final ResponseEntity<User> response = restTemplate.postForEntity(path, new HttpEntity<>(user), User.class);
        User friend = User.builder()
                .id(0)
                .email("yandexqwqfgw@yandex.ru")
                .name("Anatolii Programist")
                .login("AlP")
                .birthday(LocalDate.of(1976, 11, 1))
                .build();
        final ResponseEntity<User> responseFriend = restTemplate.postForEntity(path, new HttpEntity<>(friend),
                User.class);
        user = response.getBody();
        friend = responseFriend.getBody();
        String uri = path + "/" + user.getId() + "/friends" + "/" + friend.getId();
        final ResponseEntity<Void> responseFriends = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        uri = path + "/" + user.getId() + "/friends";
        final ResponseEntity<List> getFriends = restTemplate.getForEntity(uri, List.class);

        assertEquals(getFriends.getStatusCode(), HttpStatus.OK, "Неверный ответ сервера");
        assertEquals(getFriends.getBody().size(), 1, "Неверная работа программы");
    }

    @Test
    public void getCommonFriendsEmpty() {
        User user = User.builder()
                .id(0)
                .email("yandex@yandex.ru")
                .name("Valerii Programist")
                .login("VlP")
                .birthday(LocalDate.of(1956, 11, 1))
                .build();
        final ResponseEntity<User> response = restTemplate.postForEntity(path, new HttpEntity<>(user), User.class);
        User friend = User.builder()
                .id(0)
                .email("yandexqwqfgw@yandex.ru")
                .name("Anatolii Programist")
                .login("AlP")
                .birthday(LocalDate.of(1976, 11, 1))
                .build();
        final ResponseEntity<User> responseFriend = restTemplate.postForEntity(path, new HttpEntity<>(friend),
                User.class);
        user = response.getBody();
        friend = responseFriend.getBody();
        String uri = path + "/" + user.getId() + "/friends" + "/" + friend.getId();
        final ResponseEntity<Void> responseFriends = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        uri = path + "/" + user.getId() + "/friends" + "/common" + "/" + friend.getId();
        final ResponseEntity<List> getCommonFriends = restTemplate.getForEntity(uri, List.class);

        assertEquals(getCommonFriends.getStatusCode(), HttpStatus.OK, "Неверный ответ сервера");
        assertEquals(getCommonFriends.getBody().size(), 0, "Неверная работа программы");
    }

    @Test
    public void getCommonFriend() {
        User user = User.builder()
                .id(0)
                .email("yandex@yandex.ru")
                .name("Valerii Programist")
                .login("VlP")
                .birthday(LocalDate.of(1956, 11, 1))
                .build();
        final ResponseEntity<User> response = restTemplate.postForEntity(path, new HttpEntity<>(user), User.class);
        User friend = User.builder()
                .id(0)
                .email("yandexqwqfgw@yandex.ru")
                .name("Anatolii Programist")
                .login("AlP")
                .birthday(LocalDate.of(1976, 11, 1))
                .build();
        final ResponseEntity<User> responseFriend = restTemplate.postForEntity(path,
                new HttpEntity<>(friend), User.class);
        User other = User.builder()
                .id(0)
                .email("qwqfgw@yandex.ru")
                .name("Natalia Programist")
                .login("AlP")
                .birthday(LocalDate.of(1976, 11, 1))
                .build();
        final ResponseEntity<User> responseOther = restTemplate.postForEntity(path,
                new HttpEntity<>(other), User.class);
        user = response.getBody();
        friend = responseFriend.getBody();
        other = responseOther.getBody();
        String uri = path + "/" + user.getId() + "/friends" + "/" + friend.getId();
        final ResponseEntity<Void> responseUserFriend = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        uri = path + "/" + user.getId() + "/friends" + "/" + other.getId();
        final ResponseEntity<Void> responseUserOther = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        uri = path + "/" + friend.getId() + "/friends" + "/" + other.getId();
        final ResponseEntity<Void> responseFriendOther = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        uri = path + "/" + user.getId() + "/friends" + "/common" + "/" + friend.getId();
        final ResponseEntity<List> getCommonFriends = restTemplate.getForEntity(uri, List.class);

        assertEquals(getCommonFriends.getStatusCode(), HttpStatus.OK, "Неверный ответ сервера");
        assertEquals(getCommonFriends.getBody().size(), 1, "Неверная работа программы");
    }

    @Test
    public void getSomeFriends() {
        User user = User.builder()
                .id(0)
                .email("yandex@yandex.ru")
                .name("Valerii Programist")
                .login("VlP")
                .birthday(LocalDate.of(1956, 11, 1))
                .build();
        final ResponseEntity<User> response = restTemplate.postForEntity(path, new HttpEntity<>(user), User.class);
        User friend = User.builder()
                .id(0)
                .email("yandexqwqfgw@yandex.ru")
                .name("Anatolii Programist")
                .login("AlP")
                .birthday(LocalDate.of(1976, 11, 1))
                .build();
        final ResponseEntity<User> responseFriend = restTemplate.postForEntity(path,
                new HttpEntity<>(friend), User.class);
        User other = User.builder()
                .id(0)
                .email("qwqfgw@yandex.ru")
                .name("Natalia Programist")
                .login("AlP")
                .birthday(LocalDate.of(1976, 11, 1))
                .build();
        final ResponseEntity<User> responseOther = restTemplate.postForEntity(path,
                new HttpEntity<>(other), User.class);
        user = response.getBody();
        friend = responseFriend.getBody();
        other = responseOther.getBody();
        String uri = path + "/" + user.getId() + "/friends" + "/" + friend.getId();
        final ResponseEntity<Void> responseUserFriend = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        uri = path + "/" + user.getId() + "/friends" + "/" + other.getId();
        final ResponseEntity<Void> responseUserOther = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        uri = path + "/" + friend.getId() + "/friends" + "/" + other.getId();
        final ResponseEntity<Void> responseFriendOther = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        uri = path + "/" + user.getId() + "/friends";
        final ResponseEntity<List> getFriendsUser = restTemplate.getForEntity(uri, List.class);
        uri = path + "/" + other.getId() + "/friends";
        final ResponseEntity<List> getFriendsOther = restTemplate.getForEntity(uri, List.class);

        assertEquals(getFriendsUser.getStatusCode(), HttpStatus.OK, "Неверный ответ сервера");
        assertEquals(getFriendsUser.getBody().size(), 2, "Неверная работа программы");
        assertEquals(getFriendsOther.getBody().size(), getFriendsUser.getBody().size(),
                "Возвращает ошибочные списки друзей");
    }

    @Test
    public void deleteFriendNormal() {
        User user = User.builder()
                .id(0)
                .email("yandex@yandex.ru")
                .name("Valerii Programist")
                .login("VlP")
                .birthday(LocalDate.of(1956, 11, 1))
                .build();
        final ResponseEntity<User> response = restTemplate.postForEntity(path, new HttpEntity<>(user), User.class);
        User friend = User.builder()
                .id(0)
                .email("yandexqwqfgw@yandex.ru")
                .name("Anatolii Programist")
                .login("AlP")
                .birthday(LocalDate.of(1976, 11, 1))
                .build();
        final ResponseEntity<User> responseFriend = restTemplate.postForEntity(path,
                new HttpEntity<>(friend), User.class);
        User other = User.builder()
                .id(0)
                .email("qwqfgw@yandex.ru")
                .name("Natalia Programist")
                .login("AlP")
                .birthday(LocalDate.of(1976, 11, 1))
                .build();
        final ResponseEntity<User> responseOther = restTemplate.postForEntity(path,
                new HttpEntity<>(other), User.class);
        user = response.getBody();
        friend = responseFriend.getBody();
        other = responseOther.getBody();
        String uri = path + "/" + user.getId() + "/friends" + "/" + friend.getId();
        final ResponseEntity<Void> responseUserFriend = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        uri = path + "/" + user.getId() + "/friends" + "/" + other.getId();
        final ResponseEntity<Void> responseUserOther = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        uri = path + "/" + friend.getId() + "/friends" + "/" + other.getId();
        final ResponseEntity<Void> responseFriendOther = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        uri = path + "/" + user.getId() + "/friends" + "/" + friend.getId();
        final ResponseEntity<Void> responseUserFriendDel = restTemplate.exchange(uri, HttpMethod.DELETE,
                HttpEntity.EMPTY, Void.class);
        uri = path + "/" + user.getId() + "/friends";
        final ResponseEntity<List> getFriendsUser = restTemplate.getForEntity(uri, List.class);
        uri = path + "/" + user.getId() + "/friends" + "/common" + "/" + other.getId();
        final ResponseEntity<List> getCommonFriends = restTemplate.getForEntity(uri, List.class);

        assertEquals(HttpStatus.OK, responseUserFriendDel.getStatusCode(), "Неверный ответ сервера");
        assertEquals(getFriendsUser.getBody().size(), 1, "Неверная работа программы");
        assertEquals(getCommonFriends.getBody().size(), 0, "Неверный список общих друзей");
    }

    @Test
    public void deleteFriendUnknown() {
        User user = User.builder()
                .id(0)
                .email("yandex@yandex.ru")
                .name("Valerii Programist")
                .login("VlP")
                .birthday(LocalDate.of(1956, 11, 1))
                .build();
        final ResponseEntity<User> response = restTemplate.postForEntity(path, new HttpEntity<>(user), User.class);
        User friend = User.builder()
                .id(0)
                .email("yandexqwqfgw@yandex.ru")
                .name("Anatolii Programist")
                .login("AlP")
                .birthday(LocalDate.of(1976, 11, 1))
                .build();
        final ResponseEntity<User> responseFriend = restTemplate.postForEntity(path,
                new HttpEntity<>(friend), User.class);
        User other = User.builder()
                .id(0)
                .email("qwqfgw@yandex.ru")
                .name("Natalia Programist")
                .login("AlP")
                .birthday(LocalDate.of(1976, 11, 1))
                .build();
        final ResponseEntity<User> responseOther = restTemplate.postForEntity(path,
                new HttpEntity<>(other), User.class);
        user = response.getBody();
        friend = responseFriend.getBody();
        other = responseOther.getBody();
        String uri = path + "/" + user.getId() + "/friends" + "/" + friend.getId();
        final ResponseEntity<Void> responseUserFriend = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        uri = path + "/" + user.getId() + "/friends" + "/" + other.getId();
        final ResponseEntity<Void> responseUserOther = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        uri = path + "/" + friend.getId() + "/friends" + "/" + other.getId();
        final ResponseEntity<Void> responseFriendOther = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        uri = path + "/" + user.getId() + "/friends" + "/" + 5235235;
        final ResponseEntity<Void> responseUnknownDel = restTemplate.exchange(uri, HttpMethod.DELETE,
                HttpEntity.EMPTY, Void.class);

        assertEquals(HttpStatus.NOT_FOUND, responseUnknownDel.getStatusCode(), "Неверный ответ сервера");
    }

    @Test
    public void deleteFriendRepeat() {
        User user = User.builder()
                .id(0)
                .email("yandex@yandex.ru")
                .name("Valerii Programist")
                .login("VlP")
                .birthday(LocalDate.of(1956, 11, 1))
                .build();
        final ResponseEntity<User> response = restTemplate.postForEntity(path, new HttpEntity<>(user), User.class);
        User friend = User.builder()
                .id(0)
                .email("yandexqwqfgw@yandex.ru")
                .name("Anatolii Programist")
                .login("AlP")
                .birthday(LocalDate.of(1976, 11, 1))
                .build();
        final ResponseEntity<User> responseFriend = restTemplate.postForEntity(path,
                new HttpEntity<>(friend), User.class);
        User other = User.builder()
                .id(0)
                .email("qwqfgw@yandex.ru")
                .name("Natalia Programist")
                .login("AlP")
                .birthday(LocalDate.of(1976, 11, 1))
                .build();
        final ResponseEntity<User> responseOther = restTemplate.postForEntity(path,
                new HttpEntity<>(other), User.class);
        user = response.getBody();
        friend = responseFriend.getBody();
        other = responseOther.getBody();
        String uri = path + "/" + user.getId() + "/friends" + "/" + friend.getId();
        final ResponseEntity<Void> responseUserFriend = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        uri = path + "/" + user.getId() + "/friends" + "/" + other.getId();
        final ResponseEntity<Void> responseUserOther = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        uri = path + "/" + friend.getId() + "/friends" + "/" + other.getId();
        final ResponseEntity<Void> responseFriendOther = restTemplate.exchange(uri, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        uri = path + "/" + user.getId() + "/friends" + "/" + friend.getId();
        final ResponseEntity<Void> responseUserFriendDel = restTemplate.exchange(uri, HttpMethod.DELETE,
                HttpEntity.EMPTY, Void.class);
        final ResponseEntity<Void> responseUserFriendDelRepeat = restTemplate.exchange(uri, HttpMethod.DELETE,
                HttpEntity.EMPTY, Void.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseUserFriendDelRepeat.getStatusCode(),
                "Неверный ответ сервера");
    }
}

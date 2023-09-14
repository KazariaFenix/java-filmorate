package ru.yandex.practicum.filmorate.FilmorateDatabaseTest;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmorateDatabaseTest {
    private final UserStorage userStorage;
    private final UserService userService;
    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private final MPAStorage mpaService;
    private final GenreStorage genreService;
    User firstUser;
    User secondUser;
    User thirdUser;
    User fourthUser;
    Film firstFilm;
    Film secondFilm;
    Film thirdFilm;
    FilmMPA mpaFilm;
    Set<FilmGenre> genresFilm;


    @BeforeEach
    public void createObjects() {
        firstUser = User.builder()
                .email("First@yandex.ru")
                .login("first")
                .name("One Firstov")
                .birthday(LocalDate.of(1978, 3, 15))
                .build();
        secondUser = User.builder()
                .email("Second@yandex.ru")
                .login("second")
                .name("Two Secondov")
                .birthday(LocalDate.of(1999, 7, 10))
                .build();
        thirdUser = User.builder()
                .email("Third@yandex.ru")
                .login("third")
                .name("Three Thirdov")
                .birthday(LocalDate.of(2005, 1, 28))
                .build();
        fourthUser = User.builder()
                .email("Fourth@yandex.ru")
                .login("fourth")
                .name(" ")
                .birthday(LocalDate.of(2002, 1, 1))
                .build();
        mpaFilm = FilmMPA.builder()
                .id(3)
                .build();
        genresFilm = Set.of(FilmGenre.builder()
                        .id(1)
                        .build(),
                FilmGenre.builder()
                        .id(5)
                        .build());
        firstFilm = Film.builder()
                .name("First Film")
                .description("Nice Film")
                .duration(125)
                .releaseDate(LocalDate.of(1905, 5, 24))
                .mpa(mpaFilm)
                .genres(genresFilm)
                .build();
        secondFilm = Film.builder()
                .name("Second Film")
                .description("Beautiful Film")
                .duration(100)
                .releaseDate(LocalDate.of(2020, 7, 15))
                .mpa(FilmMPA.builder()
                        .id(1)
                        .build())
                .build();
        thirdFilm = Film.builder()
                .name("Third Film")
                .description("Very Good Film")
                .duration(240)
                .releaseDate(LocalDate.of(2000, 5, 11))
                .mpa(FilmMPA.builder()
                        .id(4)
                        .build())
                .build();
    }

    @Test
    public void addUserNormal() {
        firstUser = userStorage.addUser(firstUser);
        Optional<User> userOptional = Optional.ofNullable(userStorage.findUserById(firstUser.getId()));

        assertThat(userOptional)
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", user.getId())
                                .hasFieldOrPropertyWithValue("email", "First@yandex.ru")
                                .hasFieldOrPropertyWithValue("name", "One Firstov")
                                .hasFieldOrPropertyWithValue("login", "first")
                                .hasFieldOrPropertyWithValue("birthday",
                                        LocalDate.of(1978, 3, 15)));
    }

    @Test
    public void addUserWithBlankName() {
        fourthUser = userStorage.addUser(fourthUser);
        Optional<User> userOptional = Optional.ofNullable(userStorage.findUserById(fourthUser.getId()));

        assertThat(userOptional)
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", user.getId())
                                .hasFieldOrPropertyWithValue("email", "Fourth@yandex.ru")
                                .hasFieldOrPropertyWithValue("name", "fourth")
                                .hasFieldOrPropertyWithValue("login", "fourth")
                                .hasFieldOrPropertyWithValue("birthday",
                                        LocalDate.of(2002, 1, 1)));
    }

    @Test
    public void updateUserNormal() {
        thirdUser = userStorage.addUser(thirdUser);
        secondUser = userStorage.updateUser(thirdUser);

        Optional<User> userOptional = Optional.ofNullable(userStorage.findUserById(secondUser.getId()));
        assertThat(userOptional)
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", user.getId())
                                .hasFieldOrPropertyWithValue("email", "Third@yandex.ru")
                                .hasFieldOrPropertyWithValue("name", "Three Thirdov")
                                .hasFieldOrPropertyWithValue("login", "third")
                                .hasFieldOrPropertyWithValue("birthday",
                                        LocalDate.of(2005, 1, 28)));
    }

    @Test
    public void updateUserWithBlankName() {
        fourthUser = userStorage.addUser(fourthUser);
        firstUser = userStorage.updateUser(fourthUser);

        Optional<User> userOptional = Optional.ofNullable(userStorage.findUserById(firstUser.getId()));
        assertThat(userOptional)
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", user.getId())
                                .hasFieldOrPropertyWithValue("email", "Fourth@yandex.ru")
                                .hasFieldOrPropertyWithValue("name", "fourth")
                                .hasFieldOrPropertyWithValue("login", "fourth")
                                .hasFieldOrPropertyWithValue("birthday",
                                        LocalDate.of(2002, 1, 1)));
    }

    @Test
    public void putFriendNormal() {
        firstUser = userStorage.addUser(firstUser);
        secondUser = userStorage.addUser(secondUser);
        userStorage.putFriend((int) (firstUser.getId()), (int) secondUser.getId());
        firstUser = userStorage.findUserById(firstUser.getId());

        List<User> listFriends = userStorage.getFriendsList(firstUser.getId());

        assertThat(listFriends).asList().hasSize(1);
        assertThat(listFriends).asList().contains(userStorage.findUserById(secondUser.getId()));
        assertThat(Optional.of(listFriends.get(0)))
                .hasValueSatisfying(user ->
                        AssertionsForClassTypes.assertThat(user)
                                .hasFieldOrPropertyWithValue("email", "Second@yandex.ru"));
    }

    @Test
    public void deleteFriendNormal() {
        thirdUser = userStorage.addUser(thirdUser);
        fourthUser = userStorage.addUser(fourthUser);

        userService.putFriend((int) thirdUser.getId(), (int) fourthUser.getId());
        userService.deleteFriend((int) thirdUser.getId(), (int) fourthUser.getId());
        List<User> listFriends = userService.getFriendsList(thirdUser.getId());

        assertThat(listFriends).asList().hasSize(0);
        assertThat(listFriends).asList().doesNotContain(firstUser);
    }

    @Test
    public void getCommonFriendsNormal() {
        firstUser = userStorage.addUser(firstUser);
        secondUser = userStorage.addUser(secondUser);
        thirdUser = userStorage.addUser(thirdUser);

        userService.putFriend((int) firstUser.getId(), (int) thirdUser.getId());
        userService.putFriend((int) secondUser.getId(), (int) thirdUser.getId());
        List<User> mutualFriends = userService.getMutualFriends((int) firstUser.getId(), (int) secondUser.getId());

        assertThat(mutualFriends).asList().hasSize(1);
        assertThat(mutualFriends).asList().contains(userStorage.findUserById(thirdUser.getId()));
        assertThat(Optional.of(mutualFriends.get(0)))
                .hasValueSatisfying(user ->
                        AssertionsForClassTypes.assertThat(user)
                                .hasFieldOrPropertyWithValue("name", "Three Thirdov"));
    }

    @Test
    public void getListUsers() {
        firstUser = userStorage.addUser(firstUser);
        secondUser = userStorage.addUser(secondUser);
        thirdUser = userStorage.addUser(thirdUser);
        List<User> listUsers = userStorage.getUserList();

        assertThat(listUsers).asList().hasSize(3);
        assertThat(listUsers).asList().contains(userStorage.findUserById(firstUser.getId()));
        assertThat(listUsers).asList().contains(userStorage.findUserById(secondUser.getId()));
        assertThat(listUsers).asList().contains(userStorage.findUserById(thirdUser.getId()));
        assertThat(Optional.of(listUsers.get(0)))
                .hasValueSatisfying(user ->
                        AssertionsForClassTypes.assertThat(user)
                                .hasFieldOrPropertyWithValue("login", "first"));
        assertThat(Optional.of(listUsers.get(1)))
                .hasValueSatisfying(user ->
                        AssertionsForClassTypes.assertThat(user)
                                .hasFieldOrPropertyWithValue("login", "second"));
        assertThat(Optional.of(listUsers.get(2)))
                .hasValueSatisfying(user ->
                        AssertionsForClassTypes.assertThat(user)
                                .hasFieldOrPropertyWithValue("login", "third"));
    }

    @Test
    public void getListUsersBlank() {
        List<User> listUsers = userStorage.getUserList();

        assertThat(listUsers).asList().hasSize(0);
        assertThat(listUsers).asList().isEmpty();
    }

    @Test
    public void addFilmNormal() {
        firstFilm = filmStorage.addFilm(firstFilm);
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.findFilmById(firstFilm.getId()));

        assertThat(filmOptional)
                .hasValueSatisfying(f ->
                        assertThat(f)
                                .hasFieldOrPropertyWithValue("id", firstFilm.getId())
                                .hasFieldOrPropertyWithValue("name", "First Film")
                                .hasFieldOrPropertyWithValue("description", "Nice Film")
                                .hasFieldOrPropertyWithValue("releaseDate",
                                        LocalDate.of(1905, 5, 24))
                                .hasFieldOrPropertyWithValue("duration",
                                        125)
                                .hasFieldOrPropertyWithValue("mpa.id", 3));
    }

    @Test
    public void updateFilmNormal() {
        firstFilm = filmStorage.addFilm(firstFilm);
        secondFilm = secondFilm.toBuilder()
                .id(1)
                .build();
        filmStorage.updateFilm(secondFilm);
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.findFilmById(firstFilm.getId()));
        assertThat(filmOptional)
                .hasValueSatisfying(f ->
                        assertThat(f)
                                .hasFieldOrPropertyWithValue("id", firstFilm.getId())
                                .hasFieldOrPropertyWithValue("name", "Second Film")
                                .hasFieldOrPropertyWithValue("description", "Beautiful Film")
                                .hasFieldOrPropertyWithValue("releaseDate",
                                        LocalDate.of(2020, 7, 15))
                                .hasFieldOrPropertyWithValue("duration",
                                        100)
                                .hasFieldOrPropertyWithValue("mpa.id", 1)
                                .hasFieldOrPropertyWithValue("genres", Set.of()));
    }

    @Test
    public void getListFilms() {
        firstFilm = filmStorage.addFilm(firstFilm);
        secondFilm = filmStorage.addFilm(secondFilm);
        thirdFilm = filmStorage.addFilm(thirdFilm);
        List<Film> listFilms = filmStorage.getFilmList();

        assertThat(listFilms).asList().hasSize(3);
        assertThat(listFilms).asList().contains(filmStorage.findFilmById(firstFilm.getId()));
        assertThat(listFilms).asList().contains(filmStorage.findFilmById(secondFilm.getId()));
        assertThat(listFilms).asList().contains(filmStorage.findFilmById(thirdFilm.getId()));
        assertThat(Optional.of(listFilms.get(0)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "First Film"));
        assertThat(Optional.of(listFilms.get(1)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "Second Film"));
        assertThat(Optional.of(listFilms.get(2)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "Third Film"));
    }

    @Test
    public void getListFilmsEmpty() {
        List<Film> listFilms = filmStorage.getFilmList();

        assertThat(listFilms).asList().hasSize(0);
        assertThat(listFilms).asList().isEmpty();
    }

    @Test
    public void addLikeNormal() {
        firstUser = userStorage.addUser(firstUser);
        firstFilm = filmStorage.addFilm(firstFilm);

        filmService.putLike(firstFilm.getId(), (int) firstUser.getId());
        firstFilm = filmStorage.findFilmById(firstFilm.getId());
        Set<Integer> like = firstFilm.getUserLike();

        assertThat(like).isEqualTo(Set.of(1));
    }

    @Test
    public void deleteLikeNormal() {
        secondUser = userStorage.addUser(secondUser);
        secondFilm = filmStorage.addFilm(secondFilm);

        filmService.putLike(secondFilm.getId(), (int) secondUser.getId());
        filmService.deleteLike(secondFilm.getId(), (int) secondUser.getId());
        secondFilm = filmStorage.findFilmById(secondFilm.getId());
        Set<Integer> like = firstFilm.getUserLike();

        assertThat(like).isEqualTo(Set.of());
    }

    @Test
    public void getPopularFilms() {
        firstUser = userStorage.addUser(firstUser);
        secondUser = userStorage.addUser(secondUser);
        firstFilm = filmStorage.addFilm(firstFilm);
        secondFilm = filmStorage.addFilm(secondFilm);

        filmService.putLike(firstFilm.getId(), (int) firstUser.getId());
        filmService.putLike(secondFilm.getId(), (int) secondUser.getId());
        filmService.putLike(secondFilm.getId(), (int) firstUser.getId());
        List<Film> listPopular = filmService.getPopularFilm(10, 0, 0);
        listPopular.forEach(System.out::println);
        assertThat(listPopular).asList().hasSize(2);
        assertThat(Optional.of(listPopular.get(0)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "Second Film"));
        assertThat(Optional.of(listPopular.get(1)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "First Film"));
    }

    @Test
    public void getAllMPA() {
        List<FilmMPA> all = mpaService.getAllMPA();
        assertThat(all).asList().hasSize(5);
        final int first = 1;
        final int second = 2;

        assertThat(all).asList().startsWith(mpaService.getMPAById(first));
        assertThat(all).asList().contains(mpaService.getMPAById(second));
    }

    @Test
    public void getMPAById() {
        final int third = 3;
        final String name = "PG-13";

        assertThat(mpaService.getMPAById(third))
                .hasFieldOrPropertyWithValue("id", 3)
                .hasFieldOrPropertyWithValue("name", name);
    }

    @Test
    public void shouldListGenres() {
        List<FilmGenre> listGenres = genreService.getAllGenre();

        assertThat(listGenres).asList().hasSize(6);
        final int first = 1;
        final int second = 2;

        assertThat(listGenres).asList().startsWith(genreService.getGenreById(first));
        assertThat(listGenres).asList().contains(genreService.getGenreById(second));
    }

    @Test
    public void getGenreById() {
        final int id = 3;
        final String name = "Мультфильм";

        assertThat(genreService.getGenreById(id))
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("name", name);
    }
}
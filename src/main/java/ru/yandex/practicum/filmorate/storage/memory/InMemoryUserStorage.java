package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Repository
class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> userMap = new LinkedHashMap<>();
    private int idUser = 0;

    @Override
    public List<User> getUserList() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User addUser(User user) {
        if (userMap.containsKey(user.getId())) {
            throw new NoSuchElementException("user");
        }
        user = buildIdUser(user);
        user = buildNameUser(user);
        userMap.put((int) user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!userMap.containsKey(user.getId())) {
            throw new IllegalArgumentException("Данный юзер не существует");
        }
        user = buildNameUser(user);
        userMap.put((int) user.getId(), user);
        return user;
    }

    @Override
    public User findUserById(long userId) {
        if (userMap.get(userId) != null) {
            return userMap.get(userId);
        } else {
            throw new NoSuchElementException("userId");
        }
    }

    @Override
    public List<User> getFriendsList(long userId) {
        final List<User> friends = new ArrayList<>();
        final User user = findUserById(userId);

        for (Integer friend : user.getFriends()) {
            friends.add(findUserById(friend));
        }
        return friends;
    }

    @Override
    public void putFriend(int userId, int friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);

        if (user.getFriends().contains(friendId)) {
            throw new IllegalArgumentException("Данный пользователь уже добавлен в друзья");
        }

        user = user.toBuilder()
                .friend(friendId)
                .build();
        friend = friend.toBuilder()
                .friend(userId)
                .build();

        userMap.put(userId, user);
        userMap.put(friendId, friend);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);

        if (!user.getFriends().contains(friendId)) {
            throw new IllegalArgumentException("Данный пользователь еще не добавлен в друзья");
        }
        final List<Integer> userList = user.getFriends().stream()
                .filter(integer -> !integer.equals(friendId))
                .collect(Collectors.toList());
        final List<Integer> friendList = friend.getFriends().stream()
                .filter(integer -> !integer.equals(userId))
                .collect(Collectors.toList());
        user = user.toBuilder()
                .clearFriends()
                .friends(userList)
                .build();
        friend = friend.toBuilder()
                .clearFriends()
                .friends(friendList)
                .build();

        userMap.put(userId, user);
        userMap.put(friendId, friend);
    }

    @Override
    public List<User> getMutualFriends(int userId, int otherId) {
        final List<User> mutualFriends = new ArrayList<>();
        final User user = findUserById(userId);
        final User other = findUserById(otherId);

        for (Integer friend : user.getFriends()) {
            if (other.getFriends().contains(friend)) {
                mutualFriends.add(findUserById(friend));
            }
        }
        return mutualFriends;
    }

    @Override
    public void deleteUser(int id) {
        id = 0;
    }

    private User buildIdUser(User user) {
        idUser++;
        return user.toBuilder().id(idUser).friends(new ArrayList<>()).build();
    }

    private User buildNameUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user = user.toBuilder().name(user.getLogin()).friends(user.getFriends()).build();
        }
        if (user.getFriends() == null) {
            user = user.toBuilder().friends(userMap.get(user.getId()).getFriends()).build();
        }
        return user;
    }
}

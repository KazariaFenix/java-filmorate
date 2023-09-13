CREATE TABLE IF NOT EXISTS mpa(
mpa_id integer PRIMARY KEY,
title varchar(5)
);

CREATE TABLE IF NOT EXISTS events(
event_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
time bigint,
user_id integer,
entity_id integer,
event_type varchar(16),
operation varchar(8)
);

CREATE TABLE IF NOT EXISTS films(
film_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name varchar(64) NOT NULL,
description varchar(200),
rate integer,
release_date date NOT NULL,
duration integer,
mpa_id integer REFERENCES mpa(mpa_id) ON DELETE CASCADE,
CONSTRAINT constr_duration CHECK(duration > 0),
CONSTRAINT constr_release_date CHECK(release_date > '1895-12-28'),
CONSTRAINT constr_title CHECK(name <> '')
);

CREATE TABLE IF NOT EXISTS users(
id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
email varchar(64) NOT NULL,
name varchar(64),
login varchar(16) NOT NULL,
birthday date NOT NULL,
CONSTRAINT constr_unique UNIQUE(email, login),
CONSTRAINT constr_not_blank CHECK(email <> '' AND login <> '')
);

CREATE TABLE IF NOT EXISTS directors(
director_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name varchar(64) NOT NULL,
CONSTRAINT name UNIQUE(name)
);

CREATE TABLE IF NOT EXISTS films_directors(
film_id int REFERENCES films (film_id) ON DELETE CASCADE,
director_id int REFERENCES directors (director_id) ON DELETE CASCADE,
CONSTRAINT film_director PRIMARY KEY(film_id, director_id)
);

CREATE TABLE IF NOT EXISTS reviews(
review_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
content varchar(256),
is_positive boolean,
useful integer,
film_id int REFERENCES films (film_id) ON DELETE CASCADE,
user_id int REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews_users(
user_id int REFERENCES users (id) ON DELETE CASCADE,
review_id int REFERENCES reviews (review_id) ON DELETE CASCADE,
CONSTRAINT user_review PRIMARY KEY(user_id, review_id)
);

CREATE TABLE IF NOT EXISTS users_like(
film_id int REFERENCES films (film_id) ON DELETE CASCADE,
user_id int REFERENCES users (id) ON DELETE CASCADE,
CONSTRAINT film_user PRIMARY KEY(film_id, user_id)
);

CREATE TABLE IF NOT EXISTS user_friends(
user_id integer,
friend_id integer,
CONSTRAINT user_example_to_users FOREIGN KEY (user_id) REFERENCES users ON DELETE CASCADE,
CONSTRAINT friend_example_to_users FOREIGN KEY (friend_id) REFERENCES users ON DELETE CASCADE,
PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS genre(
genre_id integer PRIMARY KEY,
name varchar(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS films_genre(
film_id integer REFERENCES films(film_id)  ON DELETE CASCADE,
genre_id integer REFERENCES genre(genre_id)  ON DELETE CASCADE,
PRIMARY KEY(film_id, genre_id)
);
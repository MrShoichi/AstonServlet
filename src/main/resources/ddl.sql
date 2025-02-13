CREATE TABLE movies
(
    id           SERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    release_date DATE,
    duration     INT,
    rating       FLOAT,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE genres
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE movie_genres
(
    movie_id INT NOT NULL REFERENCES movies (id) ON DELETE CASCADE,
    genre_id INT NOT NULL REFERENCES genres (id) ON DELETE CASCADE,
    PRIMARY KEY (movie_id, genre_id)
);

CREATE TABLE actors
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    birth_date DATE,
    biography  TEXT
);

CREATE TABLE movie_actors
(
    movie_id INT NOT NULL REFERENCES movies (id) ON DELETE CASCADE,
    actor_id INT NOT NULL REFERENCES actors (id) ON DELETE CASCADE,
    PRIMARY KEY (movie_id, actor_id)
);

CREATE TABLE roles
(
    id         SERIAL PRIMARY KEY,
    name   VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(100) NOT NULL UNIQUE,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   TEXT         NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role_id    INT NOT NULL REFERENCES roles (id) ON DELETE CASCADE
);

CREATE TABLE reviews
(
    id         SERIAL PRIMARY KEY,
    user_id    INT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    movie_id   INT NOT NULL REFERENCES movies (id) ON DELETE CASCADE,
    rating     INT CHECK (rating BETWEEN 1 AND 10),
    comment    TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

package ru.shoichi.films.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieDtoTest {
    final ActorDto actorDto = ActorDto.builder()
            .id(1)
            .name("Name").build();
    final GenreDto genreDto = GenreDto.builder()
            .id(1)
            .name("Test").build();

    final MovieDto movieDto = MovieDto.builder()
            .id(1)
            .rating(3.4)
            .title("Title")
            .description("Description")
            .actors(List.of(actorDto))
            .genres(List.of(genreDto))
            .build();

    final MovieDto movieDto2 = MovieDto.builder()
            .id(1)
            .rating(3.4)
            .title("Title")
            .description("Description")
            .actors(List.of(actorDto))
            .genres(List.of(genreDto))
            .build();

    final MovieDto another = MovieDto.builder()
            .id(1)
            .rating(3.4)
            .title("Title2")
            .description("Description")
            .actors(List.of(actorDto))
            .genres(List.of(genreDto))
            .build();

    @Test
    void testEquals() {
        assertEquals(movieDto, movieDto2);
    }

    @Test
    void testNotEquals() {
        assertNotEquals(movieDto, another);
    }

    @Test
    void testBuilder() {
        MovieDto movieDto = MovieDto
                .builder()
                .id(1)
                .title("test")
                .description("Description")
                .rating(3.4).build();
        assertNotNull(movieDto);
    }

    @Test
    void testHashCode() {
        assertEquals(movieDto.hashCode(), movieDto2.hashCode());
    }
}
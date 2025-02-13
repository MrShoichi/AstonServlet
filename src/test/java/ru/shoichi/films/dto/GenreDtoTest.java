package ru.shoichi.films.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenreDtoTest {
    final GenreDto genreDto = GenreDto.builder().id(1).name("name").build();
    final GenreDto genreDto2 = GenreDto.builder().id(1).name("name").build();
    final GenreDto another = GenreDto.builder().id(1).name("another").build();

    @Test
    void testEquals() {

        assertEquals(genreDto, genreDto2);
    }

    @Test
    void testNotEquals() {
        assertNotEquals(genreDto, another);
    }

    @Test
    void testHashCode() {
        assertEquals(genreDto.hashCode(), genreDto2.hashCode());
    }
}
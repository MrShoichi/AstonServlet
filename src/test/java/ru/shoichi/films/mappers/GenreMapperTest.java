package ru.shoichi.films.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.shoichi.films.dto.GenreDto;
import ru.shoichi.films.entities.Genre;

import static org.junit.jupiter.api.Assertions.*;

class GenreMapperTest {

    private GenreMapper genreMapper;

    @BeforeEach
    void setUp() {
        genreMapper = new GenreMapper();
    }

    @Test
    void fromDto_ShouldConvertDtoToEntity() {
        GenreDto genreDto = new GenreDto();
        genreDto.setId(1);
        genreDto.setName("Comedy");

        Genre genre = genreMapper.fromDto(genreDto);

        assertNotNull(genre);
        assertEquals(genreDto.getId(), genre.getId());
        assertEquals(genreDto.getName(), genre.getName());
    }

    @Test
    void toDto_ShouldConvertEntityToDto() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Comedy");

        GenreDto genreDto = genreMapper.toDto(genre);

        assertNotNull(genreDto);
        assertEquals(genre.getId(), genreDto.getId());
        assertEquals(genre.getName(), genreDto.getName());
    }

    @Test
    void fromDto_ShouldReturnNullIfDtoIsNull() {
        Genre genre = genreMapper.fromDto(null);

        assertNull(genre);
    }

    @Test
    void toDto_ShouldReturnNullIfEntityIsNull() {
        GenreDto genreDto = genreMapper.toDto(null);

        assertNull(genreDto);
    }
}

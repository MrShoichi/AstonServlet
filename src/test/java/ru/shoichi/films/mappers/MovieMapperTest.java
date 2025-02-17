package ru.shoichi.films.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.shoichi.films.dto.ActorDto;
import ru.shoichi.films.dto.GenreDto;
import ru.shoichi.films.dto.MovieDto;
import ru.shoichi.films.entities.Actor;
import ru.shoichi.films.entities.Genre;
import ru.shoichi.films.entities.Movie;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import java.util.List;

class MovieMapperTest {

    private MovieMapper movieMapper;

    @BeforeEach
    void setUp() {
        movieMapper = new MovieMapper();
    }

    @Test
    void fromDto_ShouldConvertDtoToEntity() {
        GenreDto genreDto = new GenreDto();
        genreDto.setId(1);
        genreDto.setName("Comedy");

        ActorDto actorDto = new ActorDto();
        actorDto.setId(1);
        actorDto.setName("John Doe");

        MovieDto movieDto = MovieDto.builder()
                .id(1)
                .title("The Great Movie")
                .description("An amazing comedy movie.")
                .createdAt(new Date())
                .duration(120)
                .genres(List.of(genreDto))
                .actors(List.of(actorDto))
                .rating(8.5)
                .build();

        Movie movie = movieMapper.fromDto(movieDto);

        assertNotNull(movie);
        assertEquals(movieDto.getId(), movie.getId());
        assertEquals(movieDto.getTitle(), movie.getTitle());
        assertEquals(movieDto.getDescription(), movie.getDescription());
        assertEquals(movieDto.getCreatedAt(), movie.getCreatedAt());
        assertEquals(movieDto.getDuration(), movie.getDuration());
        assertEquals(movieDto.getRating(), movie.getRating());

        // Check that genres and actors are correctly mapped
        assertNotNull(movie.getGenres());
        assertEquals(1, movie.getGenres().size());
        assertEquals(genreDto.getId(), movie.getGenres().get(0).getId());
        assertEquals(genreDto.getName(), movie.getGenres().get(0).getName());

        assertNotNull(movie.getActors());
        assertEquals(1, movie.getActors().size());
        assertEquals(actorDto.getId(), movie.getActors().get(0).getId());
        assertEquals(actorDto.getName(), movie.getActors().get(0).getName());
    }

    @Test
    void toDto_ShouldConvertEntityToDto() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Comedy");

        Actor actor = new Actor();
        actor.setId(1);
        actor.setName("John Doe");

        Movie movie = new Movie();
        movie.setId(1);
        movie.setTitle("The Great Movie");
        movie.setDescription("An amazing comedy movie.");
        movie.setCreatedAt(new Date());
        movie.setDuration(120);
        movie.setGenres(List.of(genre));
        movie.setActors(List.of(actor));
        movie.setRating(8.5);

        MovieDto movieDto = movieMapper.toDto(movie);

        assertNotNull(movieDto);
        assertEquals(movie.getId(), movieDto.getId());
        assertEquals(movie.getTitle(), movieDto.getTitle());
        assertEquals(movie.getDescription(), movieDto.getDescription());
        assertEquals(movie.getCreatedAt(), movieDto.getCreatedAt());
        assertEquals(movie.getDuration(), movieDto.getDuration());
        assertEquals(movie.getRating(), movieDto.getRating());

        assertNotNull(movieDto.getGenres());
        assertEquals(1, movieDto.getGenres().size());
        assertEquals(genre.getId(), movieDto.getGenres().get(0).getId());
        assertEquals(genre.getName(), movieDto.getGenres().get(0).getName());

        assertNotNull(movieDto.getActors());
        assertEquals(1, movieDto.getActors().size());
        assertEquals(actor.getId(), movieDto.getActors().get(0).getId());
        assertEquals(actor.getName(), movieDto.getActors().get(0).getName());
    }

    @Test
    void fromDto_ShouldReturnNullIfDtoIsNull() {
        Movie movie = movieMapper.fromDto(null);

        assertNull(movie);
    }

    @Test
    void toDto_ShouldReturnNullIfEntityIsNull() {
        MovieDto movieDto = movieMapper.toDto(null);

        assertNull(movieDto);
    }

    @Test
    void fromDto_ShouldHandleEmptyGenresAndActors() {
        MovieDto movieDto = MovieDto.builder()
                .id(1)
                .title("The Great Movie")
                .description("An amazing movie.")
                .createdAt(new Date())
                .duration(120)
                .genres(List.of())
                .actors(List.of())
                .rating(8.5)
                .build();

        Movie movie = movieMapper.fromDto(movieDto);

        assertNotNull(movie);
        assertTrue(movie.getGenres().isEmpty());
        assertTrue(movie.getActors().isEmpty());
    }

    @Test
    void toDto_ShouldHandleEmptyGenresAndActors() {
        Movie movie = new Movie();
        movie.setId(1);
        movie.setTitle("The Great Movie");
        movie.setDescription("An amazing movie.");
        movie.setCreatedAt(new Date());
        movie.setDuration(120);
        movie.setGenres(List.of());  // Empty genres
        movie.setActors(List.of());  // Empty actors
        movie.setRating(8.5);

        MovieDto movieDto = movieMapper.toDto(movie);

        assertNotNull(movieDto);
        assertTrue(movieDto.getGenres().isEmpty());
        assertTrue(movieDto.getActors().isEmpty());
    }
}

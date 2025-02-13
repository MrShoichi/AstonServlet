package ru.shoichi.films.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.shoichi.films.dto.ReviewCreatedDto;
import ru.shoichi.films.entities.Movie;
import ru.shoichi.films.entities.Review;
import ru.shoichi.films.entities.Role;
import ru.shoichi.films.entities.User;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

class ReviewMapperTest {

    private ReviewMapper reviewMapper;

    @BeforeEach
    void setUp() {
        reviewMapper = new ReviewMapper();
    }

    @Test
    void fromDto_ShouldConvertDtoToEntity() {
        ReviewCreatedDto reviewCreatedDto = new ReviewCreatedDto();
        reviewCreatedDto.setId(1);
        reviewCreatedDto.setUserId(2);
        reviewCreatedDto.setMovieId(3);
        reviewCreatedDto.setRating(4.5);
        reviewCreatedDto.setComment("Great movie!");
        reviewCreatedDto.setCreatedAt(new Date());

        Review review = reviewMapper.fromDto(reviewCreatedDto);

        assertNotNull(review);
        assertEquals(reviewCreatedDto.getId(), review.getId());
        assertEquals(reviewCreatedDto.getRating(), review.getRating());
        assertEquals(reviewCreatedDto.getComment(), review.getComment());
        assertEquals(reviewCreatedDto.getCreatedAt(), review.getCreatedAt());

        assertNotNull(review.getUser());
        assertEquals(reviewCreatedDto.getUserId(), review.getUser().getId());

        assertNotNull(review.getMovie());
        assertEquals(reviewCreatedDto.getMovieId(), review.getMovie().getId());
    }

    @Test
    void toDto_ShouldConvertEntityToDto() {
        User user = new User();
        user.setId(1);
        user.setRole(new Role());

        Movie movie = new Movie();
        movie.setId(3);

        Review review = new Review();
        review.setId(1);
        review.setRating(4.5);
        review.setComment("Great movie!");
        review.setCreatedAt(new Date());
        review.setUser(user);
        review.setMovie(movie);

        ReviewCreatedDto reviewCreatedDto = reviewMapper.toDto(review);

        assertNotNull(reviewCreatedDto);
        assertEquals(review.getId(), reviewCreatedDto.getId());
        assertEquals(review.getRating(), reviewCreatedDto.getRating());
        assertEquals(review.getComment(), reviewCreatedDto.getComment());
        assertEquals(review.getCreatedAt(), reviewCreatedDto.getCreatedAt());

        assertNotNull(reviewCreatedDto.getUserId());
        assertEquals(user.getId(), reviewCreatedDto.getUserId());

        assertNotNull(reviewCreatedDto.getMovieId());
        assertEquals(movie.getId(), reviewCreatedDto.getMovieId());
    }

    @Test
    void fromDto_ShouldReturnNullIfDtoIsNull() {
        Review review = reviewMapper.fromDto(null);

        assertNull(review);
    }

    @Test
    void toDto_ShouldReturnNullIfEntityIsNull() {
        ReviewCreatedDto reviewCreatedDto = reviewMapper.toDto(null);

        assertNull(reviewCreatedDto);
    }

    @Test
    void fromDto_ShouldHandleMissingFields() {
        ReviewCreatedDto reviewCreatedDto = new ReviewCreatedDto();
        reviewCreatedDto.setId(1);
        reviewCreatedDto.setUserId(2);
        reviewCreatedDto.setMovieId(3);
        reviewCreatedDto.setRating(4.5);

        Review review = reviewMapper.fromDto(reviewCreatedDto);

        assertNotNull(review);
        assertEquals(reviewCreatedDto.getId(), review.getId());
        assertEquals(reviewCreatedDto.getRating(), review.getRating());
        assertNull(review.getComment());
        assertNull(review.getCreatedAt());
    }

    @Test
    void toDto_ShouldHandleMissingFields() {
        Review review = new Review();
        review.setId(1);
        review.setRating(4.5);
        Movie movie = new Movie();
        movie.setId(3);
        review.setMovie(movie);
        User user = new User();
        user.setId(1);
        user.setRole(new Role());
        review.setUser(user);

        ReviewCreatedDto reviewCreatedDto = reviewMapper.toDto(review);

        assertNotNull(reviewCreatedDto);
        assertEquals(review.getId(), reviewCreatedDto.getId());
        assertEquals(review.getRating(), reviewCreatedDto.getRating());
        assertNull(reviewCreatedDto.getComment());
        assertNull(reviewCreatedDto.getCreatedAt());
    }
}

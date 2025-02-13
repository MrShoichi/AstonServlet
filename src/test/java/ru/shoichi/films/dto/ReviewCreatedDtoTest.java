package ru.shoichi.films.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewCreatedDtoTest {
    final ReviewCreatedDto reviewCreatedDto = ReviewCreatedDto.builder()
            .id(1)
            .rating(4.5)
            .comment("name")
            .build();

    final ReviewCreatedDto reviewCreatedDto2 = ReviewCreatedDto.builder()
            .id(1)
            .rating(4.5)
            .comment("name")
            .build();

    final ReviewCreatedDto anotherReviewCreatedDto2 = ReviewCreatedDto.builder()
            .id(1)
            .rating(4.5)
            .comment("name2")
            .build();
    @Test
    void testEquals() {
        assertEquals(reviewCreatedDto, reviewCreatedDto2);
    }

    @Test
    void testBuilder() {
        ReviewCreatedDto reviewCreatedDto = ReviewCreatedDto.builder()
                .id(1)
                .comment("name")
                .rating(4.5).build();
        assertNotNull(reviewCreatedDto);
    }

    @Test
    void testNotEquals() {
        assertNotEquals(reviewCreatedDto, anotherReviewCreatedDto2);
    }

    @Test
    void testHashCode() {
        assertEquals(reviewCreatedDto.hashCode(), reviewCreatedDto2.hashCode());
    }
}
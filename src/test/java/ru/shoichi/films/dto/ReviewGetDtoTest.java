package ru.shoichi.films.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewGetDtoTest {
    final RoleDto roleDto = RoleDto.builder().id(1).name("user").build();

    final UserGetDto user = UserGetDto.copyFrom(UserBaseDto.builder()
            .id(1)
            .username("test")
            .email("asfdafds")
            .build());

    final ReviewGetDto reviewDto = ReviewGetDto.copyFrom(ReviewCreatedDto.builder()
            .id(1)
            .rating(4.5)
            .comment("name")
            .build());

    final ReviewGetDto reviewDto2 = ReviewGetDto.copyFrom(ReviewCreatedDto.builder()
            .id(1)
            .rating(4.5)
            .comment("name")
            .build());

    @Test
    void testEquals() {
        user.setRole(roleDto);

        reviewDto.setMovie(new MovieDto());
        reviewDto.setUser(user);

        reviewDto2.setMovie(new MovieDto());
        reviewDto2.setUser(user);

        assertEquals(reviewDto, reviewDto2);
    }

    @Test
    void testBuilder() {
        ReviewCreatedDto reviewGetDto = ReviewGetDto.builder().rating(4.5).comment("comment").build();
        assertNotNull(reviewGetDto);
    }

    @Test
    void testNotEquals() {
        user.setRole(RoleDto.builder().id(1).name("user").build());

        UserGetDto user2 = UserGetDto.copyFrom(UserBaseDto.builder()
                .id(1)
                .username("hdgf")
                .email("asfdafds")
                .build());
        user2.setRole(RoleDto.builder().id(1).name("user").build());

        reviewDto.setMovie(new MovieDto());
        reviewDto.setUser(user);

        reviewDto2.setMovie(new MovieDto());
        reviewDto2.setUser(user2);

        assertNotEquals(reviewDto, reviewDto2);
    }

    @Test
    void testHashCode() {
        user.setRole(roleDto);


        reviewDto.setMovie(new MovieDto());
        reviewDto.setUser(user);

        reviewDto2.setMovie(new MovieDto());
        reviewDto2.setUser(user);

        assertEquals(reviewDto.hashCode(), reviewDto2.hashCode());
    }
}
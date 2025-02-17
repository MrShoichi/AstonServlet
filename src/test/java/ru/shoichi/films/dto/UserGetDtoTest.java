package ru.shoichi.films.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserGetDtoTest {

    final UserBaseDto userBaseDto = UserBaseDto
            .builder()
            .id(1)
            .username("username")
            .password("password")
            .email("email")
            .roleId(1)
            .build();

    final UserBaseDto userBaseDto2 = UserBaseDto
            .builder()
            .id(1)
            .username("username")
            .password("password")
            .email("email")
            .roleId(1)
            .build();


    final RoleDto roleDto = RoleDto.builder().id(1).name("user").build();

    @Test
    void testEquals() {
        final UserGetDto userGetDto = UserGetDto.copyFrom(userBaseDto);
        userGetDto.setRole(roleDto);

        final UserGetDto userGetDto2 = UserGetDto.copyFrom(userBaseDto2);
        userGetDto2.setRole(roleDto);

        assertEquals(userGetDto, userGetDto2);
    }

    @Test
    void testBuilder() {
        ReviewCreatedDto reviewGetDto = ReviewGetDto.builder().rating(4.5).comment("comment").build();
        assertNotNull(reviewGetDto);
    }

    @Test
    void testNotEquals() {
        UserGetDto user = UserGetDto.copyFrom(userBaseDto);
        user.setRole(roleDto);

        UserGetDto user2 = UserGetDto.copyFrom(userBaseDto2);
        user2.setRole(RoleDto.builder().id(1).name("admin").build());


        assertNotEquals(user, user2);
    }

    @Test
    void testHashCode() {
        UserGetDto user = UserGetDto.copyFrom(userBaseDto);
        user.setRole(roleDto);

        UserGetDto user2 = UserGetDto.copyFrom(userBaseDto2);
        user2.setRole(roleDto);


        assertEquals(user.hashCode(), user2.hashCode());
    }
}
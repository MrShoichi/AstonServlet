package ru.shoichi.films.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserBaseDtoTest {
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

    final UserBaseDto anotherUser = UserBaseDto
            .builder()
            .id(1)
            .username("username2")
            .password("password")
            .email("email2")
            .roleId(1)
            .build();

    @Test
    void testEquals() {
        assertEquals(userBaseDto, userBaseDto2);
    }

    @Test
    void testBuilder() {
        final UserBaseDto userBaseDto2 = UserBaseDto.builder()
                .id(1)
                .username("name")
                .password("password")
                .email("email")
                .roleId(1).build();
        assertNotNull(userBaseDto2);
    }

    @Test
    void testNotEquals() {
        assertNotEquals(userBaseDto, anotherUser);
    }

    @Test
    void testHashCode() {
        assertEquals(userBaseDto.hashCode(), userBaseDto2.hashCode());
    }
}
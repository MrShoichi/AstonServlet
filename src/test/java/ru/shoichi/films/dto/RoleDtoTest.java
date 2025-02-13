package ru.shoichi.films.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleDtoTest {

    private final RoleDto roleDto = RoleDto.builder().id(1).name("Test").build();
    private final RoleDto roleDto2 = RoleDto.builder().id(1).name("Test").build();
    private final RoleDto anotherRole = RoleDto.builder().id(1).name("Another").build();

    @Test
    void testEquals() {
        assertEquals(roleDto, roleDto2);
    }

    @Test
    void testNotEquals() {
        assertNotEquals(roleDto, anotherRole);
    }

    @Test
    void testHashCode() {
        assertEquals(roleDto.hashCode(), roleDto2.hashCode());

    }
}
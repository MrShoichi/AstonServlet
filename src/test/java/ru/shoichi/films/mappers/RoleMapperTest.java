package ru.shoichi.films.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.shoichi.films.dto.RoleDto;
import ru.shoichi.films.entities.Role;

import static org.junit.jupiter.api.Assertions.*;

class RoleMapperTest {

    private RoleMapper roleMapper;

    @BeforeEach
    void setUp() {
        roleMapper = new RoleMapper();
    }

    @Test
    void fromDto_ShouldConvertDtoToEntity() {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(1);
        roleDto.setName("ADMIN");

        Role role = roleMapper.fromDto(roleDto);

        assertNotNull(role);
        assertEquals(roleDto.getId(), role.getId());
        assertEquals(roleDto.getName(), role.getName());
    }

    @Test
    void toDto_ShouldConvertEntityToDto() {
        Role role = new Role();
        role.setId(1);
        role.setName("ADMIN");

        RoleDto roleDto = roleMapper.toDto(role);

        assertNotNull(roleDto);
        assertEquals(role.getId(), roleDto.getId());
        assertEquals(role.getName(), roleDto.getName());
    }

    @Test
    void fromDto_ShouldReturnNullIfDtoIsNull() {
        Role role = roleMapper.fromDto(null);

        assertNull(role);
    }

    @Test
    void toDto_ShouldReturnNullIfEntityIsNull() {
        RoleDto roleDto = roleMapper.toDto(null);

        assertNull(roleDto);
    }

    @Test
    void fromDto_ShouldHandleEmptyFields() {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(1);

        Role role = roleMapper.fromDto(roleDto);

        assertNotNull(role);
        assertEquals(roleDto.getId(), role.getId());
        assertNull(role.getName());
    }

    @Test
    void toDto_ShouldHandleEmptyFields() {
        Role role = new Role();
        role.setId(1);

        RoleDto roleDto = roleMapper.toDto(role);

        assertNotNull(roleDto);
        assertEquals(role.getId(), roleDto.getId());
        assertNull(roleDto.getName());
    }
}

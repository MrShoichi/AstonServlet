package ru.shoichi.films.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.shoichi.films.dto.UserBaseDto;
import ru.shoichi.films.entities.Role;
import ru.shoichi.films.entities.User;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void fromDto_ShouldConvertDtoToEntity() {
        UserBaseDto userBaseDto = new UserBaseDto();
        userBaseDto.setId(1);
        userBaseDto.setUsername("testUser");
        userBaseDto.setPassword("password123");
        userBaseDto.setEmail("test@example.com");
        userBaseDto.setCreatedAt(new Date());
        userBaseDto.setRoleId(2);

        User user = userMapper.fromDto(userBaseDto);

        assertNotNull(user);
        assertEquals(userBaseDto.getId(), user.getId());
        assertEquals(userBaseDto.getUsername(), user.getUsername());
        assertEquals(userBaseDto.getPassword(), user.getPassword());
        assertEquals(userBaseDto.getEmail(), user.getEmail());
        assertEquals(userBaseDto.getCreatedAt(), user.getCreatedAt());
        assertNotNull(user.getRole());
        assertEquals(userBaseDto.getRoleId(), user.getRole().getId());
    }

    @Test
    void toDto_ShouldConvertEntityToDto() {
        Role role = new Role();
        role.setId(2);

        User user = new User();
        user.setId(1);
        user.setUsername("testUser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setCreatedAt(new Date());
        user.setRole(role);

        UserBaseDto userBaseDto = userMapper.toDto(user);

        assertNotNull(userBaseDto);
        assertEquals(user.getId(), userBaseDto.getId());
        assertEquals(user.getUsername(), userBaseDto.getUsername());
        assertEquals(user.getPassword(), userBaseDto.getPassword());
        assertEquals(user.getEmail(), userBaseDto.getEmail());
        assertEquals(user.getCreatedAt(), userBaseDto.getCreatedAt());
        assertNotNull(userBaseDto.getRoleId());
        assertEquals(user.getRole().getId(), userBaseDto.getRoleId());
    }

    @Test
    void fromDto_ShouldReturnNullIfDtoIsNull() {
        User user = userMapper.fromDto(null);

        assertNull(user);
    }

    @Test
    void toDto_ShouldReturnNullIfEntityIsNull() {
        UserBaseDto userBaseDto = userMapper.toDto(null);

        assertNull(userBaseDto);
    }

    @Test
    void fromDto_ShouldHandleEmptyFields() {
        UserBaseDto userBaseDto = new UserBaseDto();
        userBaseDto.setId(1);
        userBaseDto.setUsername("testUser");

        User user = userMapper.fromDto(userBaseDto);

        assertNotNull(user);
        assertEquals(userBaseDto.getId(), user.getId());
        assertEquals(userBaseDto.getUsername(), user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getEmail());
        assertNull(user.getCreatedAt());
        assertNotNull(user.getRole());
    }

    @Test
    void toDto_ShouldHandleEmptyFields() {
        User user = new User();
        user.setId(1);
        user.setUsername("testUser");
        Role role = new Role();
        role.setId(2);
        user.setRole(role);
        UserBaseDto userBaseDto = userMapper.toDto(user);

        assertNotNull(userBaseDto);
        assertEquals(user.getId(), userBaseDto.getId());
        assertEquals(user.getUsername(), userBaseDto.getUsername());
        assertNull(userBaseDto.getPassword());
        assertNull(userBaseDto.getEmail());
        assertNull(userBaseDto.getCreatedAt());
        assertNotNull(userBaseDto.getRoleId());
    }
}

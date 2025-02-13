package ru.shoichi.films.services.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import ru.shoichi.films.dto.UserBaseDto;
import ru.shoichi.films.entities.User;
import ru.shoichi.films.exceptions.NoAuthorize;
import ru.shoichi.films.exceptions.UserExistException;
import ru.shoichi.films.mappers.UserMapper;
import ru.shoichi.films.repositories.BaseRepository;
import ru.shoichi.films.utils.PasswordHasher;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private BaseRepository<User> repository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserBaseDto userDto;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword(PasswordHasher.hashPassword("password"));

        userDto = new UserBaseDto();
        userDto.setUsername("testUser");
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");
        userService = spy(new UserServiceImpl(repository, mapper));
    }

    @Test
    public void testLoginSuccess() throws NoAuthorize {
        when(repository.findAll()).thenReturn(Collections.singletonList(user));
        when(mapper.toDto(user)).thenReturn(userDto);
        UserBaseDto loggedInUser = userService.login("testUser", "password");

        assertNotNull(loggedInUser);
        assertEquals("testUser", loggedInUser.getUsername());
        verify(repository, times(1)).findAll();
    }

    @Test
    public void testLoginUserNotFound() {
        when(repository.findAll()).thenReturn(List.of());

        assertThrows(NoAuthorize.class, () -> userService.login("nonExistentUser", "password"));
    }

    @Test
    public void testLoginIncorrectPassword() {
        when(repository.findAll()).thenReturn(Collections.singletonList(user));

        assertThrows(NoAuthorize.class, () -> userService.login("testUser", "wrongPassword"));
    }

    @SneakyThrows
    @Test
    public void testSaveNewUser() {
        when(repository.filter(any())).thenReturn(List.of());
        when(repository.save(any(User.class))).thenReturn(user);
        when(mapper.fromDto(userDto)).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(userDto);

        UserBaseDto savedUser = userService.save(userDto);

        assertNotNull(savedUser);
        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    public void testSaveUserAlreadyExists() {
        when(repository.filter(any())).thenReturn(List.of(user));

        assertThrows(UserExistException.class, () -> userService.save(userDto));
    }
}

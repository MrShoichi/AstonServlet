package ru.shoichi.films.services;

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
import ru.shoichi.films.exceptions.NoEntityException;
import ru.shoichi.films.mappers.BaseMapper;
import ru.shoichi.films.repositories.BaseRepository;
import ru.shoichi.films.services.impl.UserServiceImpl;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ABaseServiceTest {

    @Mock
    private BaseRepository<User> repository;

    @Mock
    private BaseMapper<User, UserBaseDto> mapper;

    @InjectMocks
    private UserServiceImpl service;

    private User entity;
    private UserBaseDto dto;

    @SneakyThrows
    @BeforeEach
    public void setUp() {
        entity = new User();
        entity.setId(1);
        entity.setUsername("username");
        entity.setPassword("password");

        dto = new UserBaseDto();
        dto.setId(1);
        dto.setUsername("username");
        dto.setPassword("password");

        service = spy(new UserServiceImpl(repository, mapper));

    }

    @Test
    public void testGetAll() {
        when(repository.findAll()).thenReturn(Collections.singletonList(entity));
        when(mapper.toDto(entity)).thenReturn(dto);
        List<UserBaseDto> dtos = service.getAll();
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        verify(repository, times(1)).findAll();
        verify(mapper, times(1)).toDto(entity);
    }

    @Test
    public void testGetById() {
        when(mapper.toDto(entity)).thenReturn(dto);
        when(repository.findById(1)).thenReturn(entity);

        UserBaseDto result = service.getById(1);
        assertNotNull(result);
        verify(repository, times(1)).findById(1);
        verify(mapper, times(1)).toDto(entity);
    }

    @SneakyThrows
    @Test
    public void testSave()  {
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);
        when(mapper.fromDto(dto)).thenReturn(entity);

        UserBaseDto result = service.save(dto);
        assertNotNull(result);
        verify(mapper, times(1)).fromDto(dto);
        verify(repository, times(1)).save(entity);
        verify(mapper, times(1)).toDto(entity);
    }

    @Test
    public void testUpdate() throws NoEntityException {
        when(repository.update(entity)).thenReturn(entity);
        when(repository.findById(1)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);
        when(mapper.fromDto(dto)).thenReturn(entity);

        UserBaseDto result = service.update(dto);
        assertNotNull(result);
        verify(mapper, times(1)).fromDto(dto);
        verify(repository, times(1)).findById(1);
        verify(repository, times(1)).update(entity);
        verify(mapper, times(1)).toDto(entity);
    }

    @Test
    public void testUpdateThrowsNoEntityException() {
        when(repository.findById(1)).thenReturn(null);
        when(mapper.fromDto(dto)).thenReturn(entity);

        assertThrows(NoEntityException.class, () -> service.update(dto));
    }

    @Test
    public void testDelete() throws NoEntityException {
        when(repository.findById(1)).thenReturn(entity);
        when(repository.delete(1)).thenReturn(true);

        boolean result = service.delete(1);
        assertTrue(result);
        verify(repository, times(1)).findById(1);
        verify(repository, times(1)).delete(1);
    }

    @Test
    public void testDeleteThrowsNoEntityException() {
        when(repository.findById(1)).thenReturn(null);
        assertThrows(NoEntityException.class, () -> service.delete(1));
    }
}

package ru.shoichi.films.services.impl;

import lombok.SneakyThrows;
import ru.shoichi.films.dto.UserBaseDto;
import ru.shoichi.films.entities.User;
import ru.shoichi.films.exceptions.NoAuthorize;
import ru.shoichi.films.exceptions.UserExistException;
import ru.shoichi.films.mappers.BaseMapper;
import ru.shoichi.films.mappers.UserMapper;
import ru.shoichi.films.repositories.BaseRepository;
import ru.shoichi.films.repositories.impl.UserRepositoryImpl;
import ru.shoichi.films.services.ABaseService;
import ru.shoichi.films.services.UserService;
import ru.shoichi.films.utils.PasswordHasher;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;


public class UserServiceImpl extends ABaseService<User, UserBaseDto> implements UserService {
    public UserServiceImpl() {
        super(new UserRepositoryImpl(), new UserMapper());
    }

    public UserServiceImpl(BaseRepository<User> repository, BaseMapper<User, UserBaseDto> mapper) {
        super(repository, mapper);
    }

    @Override
    public UserBaseDto login(final String username, final String password) throws NoAuthorize {
        Optional<User> user = this.repository.findAll().stream().filter(x -> x.getUsername().equals(username)).findFirst();

        if (user.isEmpty()) {
            throw new NoAuthorize("Нет такого пользователя");
        }

        if (PasswordHasher.checkPassword(password, user.get().getPassword())) {
            return mapper.toDto(user.get());
        } else {
            throw new NoAuthorize("Пароль или логин не верны");
        }
    }

    @SneakyThrows
    @Override
    public UserBaseDto save(UserBaseDto dto) {
        if (!existUser(dto)) {
            throw new UserExistException("Пользователь с таким логином или почтой уже есть");
        }
        dto.setPassword(PasswordHasher.hashPassword(dto.getPassword()));
        dto.setCreatedAt(new Date());
        return super.save(dto);
    }

    private boolean existUser(UserBaseDto dto) {
        return repository.filter(x -> Objects.equals(x.getUsername(), dto.getUsername())
                || Objects.equals(x.getEmail(), dto.getEmail())).isEmpty();
    }
}

package ru.shoichi.films.services;

import ru.shoichi.films.dto.UserBaseDto;
import ru.shoichi.films.exceptions.NoAuthorize;

public interface UserService extends BaseService<UserBaseDto> {
    UserBaseDto login(String username, String password) throws NoAuthorize;
}

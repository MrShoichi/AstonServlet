package ru.shoichi.films.mappers;

import ru.shoichi.films.dto.UserBaseDto;
import ru.shoichi.films.dto.UserGetDto;
import ru.shoichi.films.entities.Role;
import ru.shoichi.films.entities.User;

public class UserMapper implements BaseMapper<User, UserBaseDto> {

    private final RoleMapper roleMapper = new RoleMapper();

    @Override
    public User fromDto(UserBaseDto dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setCreatedAt(dto.getCreatedAt());
        Role role = new Role();
        role.setId(dto.getRoleId());
        user.setRole(role);
        return user;
    }

    @Override
    public UserBaseDto toDto(User entity) {
        if (entity == null) {
            return null;
        }
        UserGetDto dto = UserGetDto.copyFrom(UserBaseDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .roleId(entity.getRole().getId())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .createdAt(entity.getCreatedAt())
                .build()
        );
        dto.setRole(roleMapper.toDto(entity.getRole()));
        return dto;
    }
}

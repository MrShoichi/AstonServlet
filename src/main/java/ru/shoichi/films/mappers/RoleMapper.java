package ru.shoichi.films.mappers;

import ru.shoichi.films.dto.RoleDto;
import ru.shoichi.films.entities.Role;

public class RoleMapper implements BaseMapper<Role, RoleDto> {
    @Override
    public Role fromDto(RoleDto dto) {
        if (dto == null) {
            return null;
        }
        Role role = new Role();
        role.setId(dto.getId());
        role.setName(dto.getName());
        return role;
    }

    @Override
    public RoleDto toDto(Role entity) {
        if (entity == null) {
            return null;
        }
        return RoleDto.builder()
                .id(entity.getId())
                .name(entity.getName()).build();
    }
}

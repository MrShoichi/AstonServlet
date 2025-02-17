package ru.shoichi.films.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserGetDto extends UserBaseDto {
    private RoleDto role;

    public static UserGetDto copyFrom(UserBaseDto user) {
        UserGetDto userGetDto = new UserGetDto();
        userGetDto.setId(user.getId());
        userGetDto.email = user.getEmail();
        userGetDto.username = user.getUsername();
        userGetDto.password = user.getPassword();
        userGetDto.createdAt = user.getCreatedAt();
        userGetDto.roleId = user.getRoleId();
        return userGetDto;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof UserGetDto userGetDto)) return false;
        return super.equals(userGetDto) && role.equals(userGetDto.role);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + role.hashCode();
    }

}

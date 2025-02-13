package ru.shoichi.films.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;
import java.util.Objects;

@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserBaseDto {
    protected Integer id;
    protected String username;
    protected String email;
    protected String password;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date createdAt;
    protected Integer roleId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserBaseDto userCreateDto)) return false;
        return Objects.equals(id, userCreateDto.id)
                && Objects.equals(username, userCreateDto.username)
                && Objects.equals(email, userCreateDto.email)
                && Objects.equals(password, userCreateDto.password)
                && Objects.equals(createdAt, userCreateDto.createdAt)
                && Objects.equals(roleId, userCreateDto.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password, createdAt, roleId);
    }
}

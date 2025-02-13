package ru.shoichi.films.dto;

import lombok.*;

import java.util.Objects;

@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private Integer id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleDto roleDto)) return false;
        return Objects.equals(id, roleDto.id) && Objects.equals(name, roleDto.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

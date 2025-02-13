package ru.shoichi.films.dto;

import lombok.*;

import java.util.Objects;


@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GenreDto {
    private Integer id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GenreDto genreDto)) return false;
        return Objects.equals(id, genreDto.id) && Objects.equals(name, genreDto.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

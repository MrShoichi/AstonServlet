package ru.shoichi.films.mappers;

import ru.shoichi.films.dto.GenreDto;
import ru.shoichi.films.entities.Genre;

public class GenreMapper implements BaseMapper<Genre, GenreDto> {
    @Override
    public Genre fromDto(GenreDto dto) {
        if (dto == null) {
            return null;
        }
        Genre genre = new Genre();
        genre.setId(dto.getId());
        genre.setName(dto.getName());
        return genre;
    }

    @Override
    public GenreDto toDto(Genre entity) {
        if (entity == null) {
            return null;
        }
        return GenreDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}

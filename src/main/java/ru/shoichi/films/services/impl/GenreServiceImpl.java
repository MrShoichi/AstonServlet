package ru.shoichi.films.services.impl;

import ru.shoichi.films.dto.GenreDto;
import ru.shoichi.films.entities.Genre;
import ru.shoichi.films.mappers.GenreMapper;
import ru.shoichi.films.repositories.impl.GenreRepositoryImpl;
import ru.shoichi.films.services.ABaseService;

public class GenreServiceImpl extends ABaseService<Genre, GenreDto> {

    public GenreServiceImpl() {
        super(new GenreRepositoryImpl(), new GenreMapper());
    }

}

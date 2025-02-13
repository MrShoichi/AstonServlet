package ru.shoichi.films.services.impl;

import ru.shoichi.films.dto.MovieDto;
import ru.shoichi.films.entities.Movie;
import ru.shoichi.films.exceptions.NotFoundRelationsException;
import ru.shoichi.films.exceptions.NotValideEntityException;
import ru.shoichi.films.mappers.MovieMapper;
import ru.shoichi.films.repositories.impl.MovieRepositoryImpl;
import ru.shoichi.films.services.ABaseService;

import java.util.Date;

public class MovieServiceImpl extends ABaseService<Movie, MovieDto> {
    public MovieServiceImpl() {
        super(new MovieRepositoryImpl(), new MovieMapper());
    }

    @Override
    public MovieDto save(MovieDto movieDto) throws NotFoundRelationsException, NotValideEntityException {
        movieDto.setCreatedAt(new Date());
        return super.save(movieDto);
    }
}

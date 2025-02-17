package ru.shoichi.films.repositories.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.shoichi.films.config.DBConnector;
import ru.shoichi.films.entities.Movie;
import ru.shoichi.films.repositories.ABaseRepository;
import ru.shoichi.films.repositories.managers.BaseEntityRelationLoader;
import ru.shoichi.films.repositories.managers.BaseRelationManager;
import ru.shoichi.films.repositories.managers.EntityMapper;

@Slf4j
@AllArgsConstructor
public class MovieRepositoryImpl extends ABaseRepository<Movie> {

    public MovieRepositoryImpl(DBConnector dbConnector, BaseRelationManager<Movie> relationManager, EntityMapper<Movie> entityMapper, BaseEntityRelationLoader<Movie> entityRelationLoader) {
        super(relationManager, entityMapper, entityRelationLoader, Movie.class, dbConnector);
    }
}

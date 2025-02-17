package ru.shoichi.films.services.impl;

import ru.shoichi.films.dto.ActorDto;
import ru.shoichi.films.entities.Actor;
import ru.shoichi.films.mappers.ActorMapper;
import ru.shoichi.films.repositories.impl.ActorRepositoryImpl;
import ru.shoichi.films.services.ABaseService;

public class ActorServiceImpl extends ABaseService<Actor, ActorDto> {
    public ActorServiceImpl() {
        super(new ActorRepositoryImpl(), new ActorMapper());
    }
}

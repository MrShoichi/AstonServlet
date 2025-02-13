package ru.shoichi.films.mappers;

import ru.shoichi.films.dto.ActorDto;
import ru.shoichi.films.entities.Actor;

public class ActorMapper implements BaseMapper<Actor, ActorDto>{
    @Override
    public Actor fromDto(ActorDto dto) {
        if (dto == null) {
            return null;
        }
        Actor actor = new Actor();
        actor.setId(dto.getId());
        actor.setName(dto.getName());
        actor.setBiography(dto.getBiography());
        actor.setBirthDate(dto.getBirthDate());
        return actor;
    }

    @Override
    public ActorDto toDto(Actor entity) {
        if (entity == null) {
            return null;
        }
        return ActorDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .biography(entity.getBiography())
                .birthDate(entity.getBirthDate())
                .build();
    }
}

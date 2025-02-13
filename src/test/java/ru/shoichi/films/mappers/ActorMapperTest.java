package ru.shoichi.films.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.shoichi.films.dto.ActorDto;
import ru.shoichi.films.entities.Actor;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

class ActorMapperTest {

    private ActorMapper actorMapper;

    @BeforeEach
    void setUp() {
        actorMapper = new ActorMapper();
    }

    @Test
    void fromDto_ShouldConvertDtoToEntity() {
        ActorDto actorDto = new ActorDto();
        actorDto.setId(1);
        actorDto.setName("John Doe");
        actorDto.setBiography("A well-known actor.");
        actorDto.setBirthDate(new Date());

        Actor actor = actorMapper.fromDto(actorDto);

        assertNotNull(actor);
        assertEquals(actorDto.getId(), actor.getId());
        assertEquals(actorDto.getName(), actor.getName());
        assertEquals(actorDto.getBiography(), actor.getBiography());
        assertEquals(actorDto.getBirthDate(), actor.getBirthDate());
    }

    @Test
    void toDto_ShouldConvertEntityToDto() {
        Actor actor = new Actor();
        actor.setId(1);
        actor.setName("John Doe");
        actor.setBiography("A well-known actor.");
        actor.setBirthDate(new Date());

        ActorDto actorDto = actorMapper.toDto(actor);

        assertNotNull(actorDto);
        assertEquals(actor.getId(), actorDto.getId());
        assertEquals(actor.getName(), actorDto.getName());
        assertEquals(actor.getBiography(), actorDto.getBiography());
        assertEquals(actor.getBirthDate(), actorDto.getBirthDate());
    }

    @Test
    void fromDto_ShouldReturnNullIfDtoIsNull() {
        Actor actor = actorMapper.fromDto(null);

        assertNull(actor);
    }

    @Test
    void toDto_ShouldReturnNullIfEntityIsNull() {
        ActorDto actorDto = actorMapper.toDto(null);

        assertNull(actorDto);
    }
}

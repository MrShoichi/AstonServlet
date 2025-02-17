package ru.shoichi.films.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActorDtoTest {

    private final ActorDto actorDto = ActorDto.builder().id(1).name("name").build();
    private final ActorDto actorDto1 = ActorDto.builder().id(1).name("name").build();
    private final ActorDto anotherActor = ActorDto.builder().id(1).name("another").build();

    @Test
    void testEquals() {

        assertEquals(actorDto, actorDto1);
    }

    @Test
    void testBuilder() {
        ActorDto actorDto = ActorDto.builder().name("test").build();
        assertNotNull(actorDto);
    }

    @Test
    void testNotEquals() {
        assertNotEquals(actorDto, anotherActor);
    }

    @Test
    void testHashCode() {
        assertEquals(actorDto1.hashCode(), actorDto.hashCode());
    }
}
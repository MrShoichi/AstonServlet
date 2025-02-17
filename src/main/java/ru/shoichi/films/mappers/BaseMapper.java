package ru.shoichi.films.mappers;

import ru.shoichi.films.entities.BaseEntity;


public interface BaseMapper<E extends BaseEntity, D> {
    E fromDto(D dto);
    D toDto(E entity);
}

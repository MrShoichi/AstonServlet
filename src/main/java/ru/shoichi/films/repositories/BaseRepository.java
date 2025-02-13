package ru.shoichi.films.repositories;

import ru.shoichi.films.entities.BaseEntity;
import ru.shoichi.films.exceptions.NotFoundRelationsException;
import ru.shoichi.films.exceptions.NotValideEntityException;

import java.util.List;
import java.util.function.Predicate;

public interface BaseRepository<E extends BaseEntity> {
    List<E> findAll();
    List<E> filter(Predicate<E> predicate);
    E findById(int id);
    E save(E entity) throws NotFoundRelationsException, NotValideEntityException;
    E update(E entity);
    boolean delete(int id);
}

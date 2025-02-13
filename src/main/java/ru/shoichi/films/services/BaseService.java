package ru.shoichi.films.services;

import ru.shoichi.films.exceptions.NoEntityException;
import ru.shoichi.films.exceptions.NotFoundRelationsException;
import ru.shoichi.films.exceptions.NotValideEntityException;

import java.util.List;

public interface BaseService<T> {
    List<T> getAll();
    T getById(Integer id);
    T save(T t) throws NotFoundRelationsException, NotValideEntityException;
    T update(T t) throws NoEntityException;
    boolean delete(Integer id) throws NoEntityException;
}

package ru.shoichi.films.repositories.managers;

import ru.shoichi.films.entities.BaseEntity;
import ru.shoichi.films.exceptions.CyclicException;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BaseEntityMapper<T extends BaseEntity> {
    T getEntity(ResultSet resultSet) throws SQLException;
    <E> E getEntity(ResultSet resultSet, E entity) throws CyclicException;
}

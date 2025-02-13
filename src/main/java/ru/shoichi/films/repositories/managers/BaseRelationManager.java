package ru.shoichi.films.repositories.managers;

import ru.shoichi.films.entities.BaseEntity;

import java.sql.Connection;
import java.sql.SQLException;

public interface BaseRelationManager<T extends BaseEntity> {
    void saveRelation(T entity, Connection connection);
    void deleteAllLastRelations(T entity, Connection connection) throws SQLException;
}

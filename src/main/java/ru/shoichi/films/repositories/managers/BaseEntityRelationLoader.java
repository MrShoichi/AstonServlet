package ru.shoichi.films.repositories.managers;

import ru.shoichi.films.entities.BaseEntity;

import java.sql.SQLException;
import java.util.List;


public interface BaseEntityRelationLoader<T> {
    <E> List<E> getRelatedEntities(int Id, Class<E> relatedEntityClass);

    void loadRelation(T entity) throws IllegalAccessException, SQLException;

    void loadRelatedEntities(Class<? extends BaseEntity> relatedEntityClass,
                             List<? extends BaseEntity> baseEntities,
                             List<? super BaseEntity> relatedEntities);
    Object getRelatedEntity(int Id, Class<?> genericType) throws SQLException;
}

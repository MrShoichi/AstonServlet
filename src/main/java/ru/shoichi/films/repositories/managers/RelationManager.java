package ru.shoichi.films.repositories.managers;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.shoichi.films.entities.BaseEntity;
import ru.shoichi.films.utils.SQLQueryBuilder;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static ru.shoichi.films.utils.RepositoryUtils.*;

@Slf4j
@AllArgsConstructor
public class RelationManager<T extends BaseEntity> implements BaseRelationManager<T> {
    @Override
    @SneakyThrows
    public void saveRelation(T entity, Connection connection) {
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (List.class.isAssignableFrom(field.getType())) {
                saveManyRelation(entity, connection, field);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void saveManyRelation(T entity, Connection connection, Field field) throws SQLException, IllegalAccessException, NoSuchFieldException {
        Class<? extends BaseEntity> relatedEntityClass = getRelatedEntityClassFromList(field);
        String relationTable = getRelationTableName(entity.getClass(), relatedEntityClass);

        deleteLastRelations(entity, relationTable, connection);

        List<? extends BaseEntity> relatedEntities = (List<? extends BaseEntity>) field.get(entity);

        if (relatedEntities != null && !relatedEntities.isEmpty()) {
            addRelations(entity, relationTable, relatedEntityClass, relatedEntities, connection);
        }
    }

    private void addRelations(T entity, String relationTable, Class<?> relatedEntityClass, List<?> relatedEntities, Connection connection) throws SQLException, NoSuchFieldException, IllegalAccessException {
        String sql = SQLQueryBuilder.insertRelation(
                relationTable,
                getForeignKeyName(entity.getClass()),
                getForeignKeyName(relatedEntityClass)
        );

        try (PreparedStatement insertStmt = connection.prepareStatement(sql)) {
            for (Object relatedEntity : relatedEntities) {
                Field idField = relatedEntity.getClass().getSuperclass().getDeclaredField("id");
                idField.setAccessible(true);
                int relatedId = (int) idField.get(relatedEntity);

                insertStmt.setInt(1, entity.getId());
                insertStmt.setInt(2, relatedId);
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
        }
    }

    @Override
    public void deleteAllLastRelations(T entity, Connection connection) throws SQLException {
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (List.class.isAssignableFrom(field.getType())) {
                Class<?> relatedEntityClass = getRelatedEntityClassFromList(field);

                String relationTable = getRelationTableName(entity.getClass(), relatedEntityClass);

                deleteLastRelations(entity, relationTable, connection);
            }
        }
    }

    private void deleteLastRelations(T entity, String relationTable, Connection connection) throws SQLException {
        String sql = SQLQueryBuilder.deleteRelation(relationTable, getForeignKeyName(entity.getClass()));
        try (PreparedStatement deleteStmt = connection.prepareStatement(sql)) {
            deleteStmt.setInt(1, entity.getId());
            deleteStmt.executeUpdate();
        }
    }



}

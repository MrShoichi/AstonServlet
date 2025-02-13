package ru.shoichi.films.repositories.managers;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.shoichi.films.config.DBConnector;
import ru.shoichi.films.entities.BaseEntity;
import ru.shoichi.films.utils.SQLQueryBuilder;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static ru.shoichi.films.utils.RepositoryUtils.*;

@Slf4j
@AllArgsConstructor
@Setter
public class EntityRelationLoader<T extends BaseEntity> implements BaseEntityRelationLoader<T> {
    private final DBConnector dbConnector;
    private EntityMapper<T> entityMapper;
    private final Class<T> mainEntityClass;

    public EntityRelationLoader(DBConnector dbConnector, Class<T> entityClass) {
        this.dbConnector = dbConnector;
        this.mainEntityClass = entityClass;
    }

    @Override
    @SneakyThrows
    public <E> List<E> getRelatedEntities(int Id, Class<E> relatedEntityClass) {
        try (Connection connection = dbConnector.GetConnection()) {
            String query = SQLQueryBuilder.selectRelationForId(
                    getTableName(relatedEntityClass),
                    getForeignKeyName(mainEntityClass),
                    getRelationTableName(mainEntityClass, relatedEntityClass),
                    getForeignKeyName(relatedEntityClass)
            );
            PreparedStatement statement = connection.prepareStatement(query);
            List<E> entities = new ArrayList<>();
            statement.setInt(1, Id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                entities.add(entityMapper.getEntity(resultSet, relatedEntityClass.getDeclaredConstructor().newInstance()));
            }
            return entities;
        } catch (SQLException e) {
            log.error("Ошибка при получении данных: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void loadRelation(T entity) throws IllegalAccessException, SQLException {
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (List.class.isAssignableFrom(field.getType())) {
                Class<? extends BaseEntity> relatedEntityClass = getRelatedEntityClassFromList(field);

                List<? extends BaseEntity> relatedEntities = (List<? extends BaseEntity>) field.get(entity);
                List<? super BaseEntity> outEntities = new ArrayList<>();

                if (relatedEntities != null && !relatedEntities.isEmpty()) {
                    loadRelatedEntities(relatedEntityClass, relatedEntities, outEntities);
                    field.set(entity, outEntities);
                }
            } else if(BaseEntity.class.isAssignableFrom(field.getType())) {
                field.set(entity, getRelatedEntity(((BaseEntity) field.get(entity)).getId(), field.getType()));
            }
        }

    }

    @Override
    @SneakyThrows
    public void loadRelatedEntities(Class<? extends BaseEntity> relatedEntityClass,
                                    List<? extends BaseEntity> baseEntities,
                                    List<? super BaseEntity> relatedEntities) {
        List<Integer> ids = baseEntities.stream().map(BaseEntity::getId).toList();
        try (Connection connection = dbConnector.GetConnection()) {
            String query = SQLQueryBuilder.selectByIds(getTableName(relatedEntityClass), ids);
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                relatedEntities.add(entityMapper.getEntity(resultSet,
                        relatedEntityClass.getDeclaredConstructor().newInstance()));
            }
        } catch (SQLException e) {
            log.error("Ошибка при получении данных: {}", e.getMessage(), e);
            throw e;
        }
    }

    @SneakyThrows
    @Override
    public Object getRelatedEntity(int Id, Class<?> relatedEntityClass) throws SQLException {
        try (Connection connection = dbConnector.GetConnection()) {
            String query = SQLQueryBuilder.selectById(getTableName(relatedEntityClass));
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return entityMapper.getEntity(resultSet, relatedEntityClass.getDeclaredConstructor().newInstance(), true);
            }
            return null;
        } catch (SQLException e) {
            log.error("Ошибка при получении данных: {}", e.getMessage(), e);
            throw e;
        }
    }


    private String getTableName(Class<?> entityType) {
        return entityType.getSimpleName().toLowerCase() + "s";
    }
}

package ru.shoichi.films.repositories;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.shoichi.films.config.DBConnector;
import ru.shoichi.films.entities.BaseEntity;
import ru.shoichi.films.exceptions.NotFoundRelationsException;
import ru.shoichi.films.exceptions.NotValideEntityException;
import ru.shoichi.films.repositories.managers.*;
import ru.shoichi.films.utils.SQLQueryBuilder;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.shoichi.films.utils.RepositoryUtils.*;

@Slf4j
@AllArgsConstructor
public abstract class ABaseRepository<T extends BaseEntity> implements BaseRepository<T> {
    protected final BaseRelationManager<T> relationManager;
    protected final EntityMapper<T> entityMapper;
    protected final BaseEntityRelationLoader<T> entityRelationLoader;
    protected final Class<T> entityClass;
    protected DBConnector dbConnector;

    public ABaseRepository() {
        this.dbConnector = new DBConnector();
        this.entityClass = getEntityClassFromRepository(getClass());
        EntityRelationLoader<T> entityRelationLoader = new EntityRelationLoader<>(dbConnector, entityClass);
        this.relationManager = new RelationManager<>();
        this.entityMapper = new EntityMapper<>(entityRelationLoader, entityClass);
        entityRelationLoader.setEntityMapper(entityMapper);
        this.entityRelationLoader = entityRelationLoader;
    }

    @SneakyThrows
    @Override
    public List<T> findAll() {
        try (Connection connection = dbConnector.GetConnection()) {
            Statement statement = connection.createStatement();
            List<T> entities = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery(SQLQueryBuilder.selectAll(getTableName(entityClass)));
            while (resultSet.next()) {
                entities.add(entityMapper.getEntity(resultSet));
            }
            return entities;
        } catch (SQLException e) {
            log.error("Ошибка при получении данных: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<T> filter(Predicate<T> predicate) {
        return findAll().stream().filter(predicate).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public T findById(int id) {
        try (Connection connection = dbConnector.GetConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLQueryBuilder.selectById(getTableName(entityClass)));
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return entityMapper.getEntity(resultSet);
            }
            return null;
        } catch (SQLException e) {
            log.error("Ошибка при получении записи по ID: {}", e.getMessage(), e);
            throw e;
        }
    }

    @SneakyThrows
    @Override
    public T save(T entity) throws NotValideEntityException, NotFoundRelationsException {
        try (Connection connection = dbConnector.GetConnection()) {
            List<String> params = getParameters(entityClass);
            String sql = SQLQueryBuilder.insert(getTableName(entityClass), params);
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setPreparedStatementParams(preparedStatement, entity);
                preparedStatement.executeUpdate();

                ResultSet generatedKey = preparedStatement.getGeneratedKeys();
                if (generatedKey.next()) {
                    entity.setId(generatedKey.getInt(1));
                } else {
                    throw new SQLException("Ошибка получения сгенерированного ID");
                }

                if (hasRelations()) {
                    relationManager.saveRelation(entity, connection);
                    entityRelationLoader.loadRelation(entity);
                }


                connection.commit();
                return entity;
            } catch (BatchUpdateException e) {
                log.error("Ошибка при сохранении данных: {}", e.getMessage(), e);
                connection.rollback();
                throw new NotFoundRelationsException("Нет связных сущностей");
            } catch (SQLException e) {
                log.error("Ошибка при сохранении данных: {}", e.getMessage(), e);
                connection.rollback();
                throw e;
            }
        }
    }


    @SneakyThrows
    @Override
    public T update(T entity) {
        try (Connection connection = dbConnector.GetConnection()) {
            List<String> params = getParameters(entityClass);
            String updateSql = SQLQueryBuilder.update(getTableName(entityClass), params);
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
                setPreparedStatementParams(preparedStatement, entity);
                preparedStatement.setInt(params.size() + 1, entity.getId()); // set ID as last parameter
                preparedStatement.executeUpdate();
                if (hasRelations()) {
                    relationManager.saveRelation(entity, connection);
                    entityRelationLoader.loadRelation(entity);
                }
                connection.commit();
                return entity;
            } catch (SQLException e) {
                log.error("Ошибка при обновлении данных: {}", e.getMessage(), e);
                connection.rollback();
                throw e;
            }
        }
    }

    @SneakyThrows
    @Override
    public boolean delete(int id) {
        try (Connection connection = dbConnector.GetConnection()) {
            String deleteSql = SQLQueryBuilder.delete(getTableName(entityClass));
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSql)) {
                preparedStatement.setInt(1, id);

                int res = preparedStatement.executeUpdate();
                if (res > 0 && hasRelations()) {
                    T entity = findById(id);
                    relationManager.deleteAllLastRelations(entity, connection);
                }
                connection.commit();
                return res > 0;
            } catch (SQLException e) {
                log.error("Ошибка при удалении данных: {}", e.getMessage(), e);
                connection.rollback();
                throw e;
            }
        }
    }

    private boolean hasRelations() {
        return Arrays.stream(entityClass
                        .getDeclaredFields())
                .map(Field::getType)
                .anyMatch(type->List.class.isAssignableFrom(type) || BaseEntity.class.isAssignableFrom(type));
    }


}


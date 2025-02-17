package ru.shoichi.films.repositories.managers;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.shoichi.films.entities.BaseEntity;
import ru.shoichi.films.exceptions.CyclicException;
import ru.shoichi.films.exceptions.UnsupportedFieldException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static ru.shoichi.films.utils.RepositoryUtils.getForeignKeyName;
import static ru.shoichi.films.utils.RepositoryUtils.toSnakeCase;

@Slf4j
@AllArgsConstructor
public class EntityMapper<T extends BaseEntity> implements BaseEntityMapper<T> {

    private final BaseEntityRelationLoader<T> relationLoader;
    private final Class<T> entityClass;


    @Override
    @SneakyThrows
    public T getEntity(ResultSet resultSet) throws SQLException {
        T entity = entityClass.getDeclaredConstructor().newInstance();
        return getEntity(resultSet, entity);
    }
    public <E> E getEntity(ResultSet resultSet, E entity) throws CyclicException {
        return getEntity(resultSet, entity, false);
    }

    public <E> E getEntity(ResultSet resultSet, E entity, boolean cyclic) throws CyclicException {
        Stream<Field> stream = Stream.concat(
                Arrays.stream(entity.getClass().getDeclaredFields()),
                Arrays.stream(entity.getClass().getSuperclass().getDeclaredFields())
        );

        stream.forEach(field -> {
            try {
                field.setAccessible(true);
                String fieldName = toSnakeCase(field.getName());
                Object value = getFieldValueFromResultSet(resultSet, fieldName, field, cyclic);
                field.set(entity, value);
            } catch (IllegalAccessException e) {
                log.error("Ошибка при доступе к полю: {}", field.getName(), e);
            } catch (SQLException e) {
                throw new UnsupportedFieldException(e.getMessage());
            } catch (CyclicException e) {
                throw e;
            }
        });

        return entity;
    }

    private Object getFieldValueFromResultSet(ResultSet resultSet, String fieldName, Field field, boolean cyclic) throws SQLException, CyclicException {
        Class<?> type = field.getType();
        if(isIgnoreMapping(field)) {
            return null;
        }
        if(cyclic && isIgnoreBackReferenceMapping(field)) {
            return null;
        }
        if (type == String.class) {
            return resultSet.getString(fieldName);
        } else if (type == int.class || type == Integer.class) {
            return resultSet.getInt(fieldName);
        } else if (type == long.class || type == Long.class) {
            return resultSet.getLong(fieldName);
        } else if (type == double.class || type == Double.class) {
            return resultSet.getDouble(fieldName);
        } else if (type == boolean.class || type == Boolean.class) {
            return resultSet.getBoolean(fieldName);
        } else if (type == Date.class) {
            return resultSet.getDate(fieldName);
        } else if (List.class.isAssignableFrom(type)) {

            Class<?> genericType = getListGenericType(field);

            if (genericType != null) {
                if (hasCyclic(genericType)) {
                    throw new CyclicException("Cyclic relation");
                }
                return relationLoader.getRelatedEntities(resultSet.getInt("id"), genericType);
            }
            return null;
        } else if (BaseEntity.class.isAssignableFrom(type)) {
            if (hasCyclic(type)) {
                throw new CyclicException("Cyclic relation");
            }
            return relationLoader.getRelatedEntity(resultSet.getInt(getForeignKeyName(type)), type);
        } else {
            throw new SQLException("Unsupported field type: " + type);
        }
    }

    private boolean hasCyclic(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                .anyMatch(t ->
                        (entityClass == t.getType()
                                || List.class.isAssignableFrom(t.getType()))
                                && !isIgnoreBackReferenceMapping(t));
    }

    private boolean isIgnoreMapping(Field field) {
        return Arrays.stream(field.getAnnotations()).anyMatch(a -> a.annotationType() == JsonIgnore.class);
    }

    private boolean isIgnoreBackReferenceMapping(Field field) {
        return Arrays.stream(field.getAnnotations()).anyMatch(a -> a.annotationType() == JsonBackReference.class);
    }

    private Class<?> getListGenericType(Field field) {
        Type genericFieldType = field.getGenericType();
        if (genericFieldType instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genericFieldType).getActualTypeArguments();
            if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof Class<?>) {
                return (Class<?>) actualTypeArguments[0];
            }
        }
        return null;
    }

}

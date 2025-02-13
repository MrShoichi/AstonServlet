package ru.shoichi.films.utils;

import ru.shoichi.films.entities.BaseEntity;
import ru.shoichi.films.exceptions.NotValideEntityException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public final class RepositoryUtils {
    public static String toSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    public static List<String> getParameters(Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(x -> !List.class.isAssignableFrom(x.getType())) // Исключаем списки
                .map(RepositoryUtils::toParameterName)
                .toList();
    }

    private static String toParameterName(Field field) {
        if(BaseEntity.class.isAssignableFrom(field.getType())) {
            return getForeignKeyName(field.getType());
        }
        return toSnakeCase(field.getName());
    }

    public static <T> void setPreparedStatementParams(PreparedStatement preparedStatement, T entity) throws SQLException, IllegalAccessException, NotValideEntityException {
        Field[] fields = entity.getClass().getDeclaredFields(); // Используем entity.getClass()
        int index = 1;
        for (Field field : fields) {
            if (field.getType() != List.class) {
                field.setAccessible(true);
                Object value = field.get(entity);
                if (value == null) {
                    throw new NotValideEntityException("Не все свойства объекта заданы");
                }

                if (value instanceof Date) {
                    preparedStatement.setTimestamp(index++, new Timestamp(((Date) value).getTime()));
                } else if (value instanceof BaseEntity) {
                    Integer id = ((BaseEntity) value).getId();
                    if (id == null) {
                        throw new NotValideEntityException("Не все свойства объекта заданы");
                    }
                    preparedStatement.setInt(index++, ((BaseEntity) value).getId());
                } else {
                    preparedStatement.setObject(index++, value);
                }
            }
        }
    }

    public static <T> Class<T> getEntityClassFromRepository(Class<?> repositoryClass) {
        ParameterizedType type = (ParameterizedType) repositoryClass.getGenericSuperclass();
        return (Class<T>) type.getActualTypeArguments()[0];
    }

    public static String getTableName(Class<?> entityClass) {
        return getEntityName(entityClass) + "s";
    }

    public static String getEntityName(Class<?> entityClass) {
        return entityClass.getSimpleName().toLowerCase();
    }

    public static String getForeignKeyName(Class<?> entityType) {
        return toSnakeCase(entityType.getSimpleName()) + "_id";
    }


    public static String getRelationTableName(Class<?> mainEntity, Class<?> relatedEntity) {
        return mainEntity.getSimpleName().toLowerCase() + "_" + relatedEntity.getSimpleName().toLowerCase() + "s";
    }

    public static Class<? extends BaseEntity> getRelatedEntityClassFromList(Field field) {
        ParameterizedType listType = (ParameterizedType) field.getGenericType();
        return (Class<? extends BaseEntity>) listType.getActualTypeArguments()[0];
    }
}


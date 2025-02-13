package ru.shoichi.films.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SQLQueryBuilder {

    private static final String SELECT_ALL = "SELECT * FROM %s";
    private static final String SELECT_BY_ID = "SELECT * FROM %s WHERE id = ?";
    private static final String SELECT_BY_IDS = "SELECT * FROM %s WHERE id IN (%s)";
    private static final String SELECT_FOR_TABLE_ID = "SELECT * FROM %s where id in (select %s from %s where %s = ?)";
    private static final String INSERT = "INSERT INTO %s (%s) VALUES (%s)";
    private static final String UPDATE = "UPDATE %s SET %s WHERE id = ?";
    private static final String DELETE = "DELETE FROM %s WHERE id = ?";
    private static final String INSERT_RELATION = "INSERT INTO %s (%s, %s) VALUES (?, ?)";
    private static final String DELETE_RELATION = "DELETE FROM %s WHERE %s = ?";

    public static String selectAll(String table) {
        return String.format(SELECT_ALL, table);
    }

    public static String selectById(String table) {
        return String.format(SELECT_BY_ID, table);
    }

    public static String selectByIds(String table, List<Integer> ids) {
        String placeholders = ids.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        return String.format(SELECT_BY_IDS, table, placeholders);
    }

    public static String selectRelationForId(String table, String mainKey, String foreignTable, String foreignColumn) {
        return String.format(SELECT_FOR_TABLE_ID, table,
                foreignColumn,
                foreignTable,
                mainKey);
    }

    public static String insert(String table, List<String> columns) {
        String columnNames = String.join(", ", columns);
        String placeholders = IntStream.range(0, columns.size())
                .mapToObj(i -> "?")
                .collect(Collectors.joining(", "));
        return String.format(INSERT, table, columnNames, placeholders);
    }

    public static String update(String table, List<String> columns) {
        String setClause = columns.stream()
                .map(column -> column + " = ?")
                .collect(Collectors.joining(", "));
        return String.format(UPDATE, table, setClause);
    }

    public static String delete(String table) {
        return String.format(DELETE, table);
    }

    public static String insertRelation(String table, String keyOne, String keyTwo) {
        return String.format(INSERT_RELATION, table, keyOne, keyTwo);
    }

    public static String deleteRelation(String table, String column) {
        return String.format(DELETE_RELATION, table, column);
    }
}

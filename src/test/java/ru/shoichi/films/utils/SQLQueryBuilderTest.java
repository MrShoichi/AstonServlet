package ru.shoichi.films.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SQLQueryBuilderTest {

    @Test
    public void testSelectAll() {
        String table = "movies";
        String expectedQuery = "SELECT * FROM movies";
        assertEquals(expectedQuery, SQLQueryBuilder.selectAll(table));
    }

    @Test
    public void testSelectById() {
        String table = "movies";
        String expectedQuery = "SELECT * FROM movies WHERE id = ?";
        assertEquals(expectedQuery, SQLQueryBuilder.selectById(table));
    }

    @Test
    public void testSelectByIds() {
        String table = "movies";
        List<Integer> ids = Arrays.asList(1, 2, 3);
        String expectedQuery = "SELECT * FROM movies WHERE id IN (1, 2, 3)";
        assertEquals(expectedQuery, SQLQueryBuilder.selectByIds(table, ids));
    }

    @Test
    public void testSelectRelationForId() {
        String table = "movies";
        String mainKey = "actor_id";
        String foreignTable = "actors";
        String foreignColumn = "id";
        String expectedQuery = "SELECT * FROM movies where id in (select id from actors where actor_id = ?)";
        assertEquals(expectedQuery, SQLQueryBuilder.selectRelationForId(table, mainKey, foreignTable, foreignColumn));
    }

    @Test
    public void testInsert() {
        String table = "movies";
        List<String> columns = Arrays.asList("name", "director", "release_date");
        String expectedQuery = "INSERT INTO movies (name, director, release_date) VALUES (?, ?, ?)";
        assertEquals(expectedQuery, SQLQueryBuilder.insert(table, columns));
    }

    @Test
    public void testUpdate() {
        String table = "movies";
        List<String> columns = Arrays.asList("name", "director");
        String expectedQuery = "UPDATE movies SET name = ?, director = ? WHERE id = ?";
        assertEquals(expectedQuery, SQLQueryBuilder.update(table, columns));
    }

    @Test
    public void testDelete() {
        String table = "movies";
        String expectedQuery = "DELETE FROM movies WHERE id = ?";
        assertEquals(expectedQuery, SQLQueryBuilder.delete(table));
    }

    @Test
    public void testInsertRelation() {
        String table = "movie_actor";
        String keyOne = "movie_id";
        String keyTwo = "actor_id";
        String expectedQuery = "INSERT INTO movie_actor (movie_id, actor_id) VALUES (?, ?)";
        assertEquals(expectedQuery, SQLQueryBuilder.insertRelation(table, keyOne, keyTwo));
    }

    @Test
    public void testDeleteRelation() {
        String table = "movie_actor";
        String column = "movie_id";
        String expectedQuery = "DELETE FROM movie_actor WHERE movie_id = ?";
        assertEquals(expectedQuery, SQLQueryBuilder.deleteRelation(table, column));
    }
}

package ru.shoichi.films.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.shoichi.films.entities.BaseEntity;
import ru.shoichi.films.entities.Genre;
import ru.shoichi.films.entities.Movie;
import ru.shoichi.films.exceptions.NotValideEntityException;
import ru.shoichi.films.repositories.ABaseRepository;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepositoryUtilsTest {

    static class TestEntity extends BaseEntity {
        private String name;
        private int age;
        private Date createdAt;
        private Genre genre;
        private List<Movie> movies;

        public TestEntity(String name, int age, Date createdAt, Genre genre) {
            this.name = name;
            this.age = age;
            this.createdAt = createdAt;
            this.genre = genre;
        }
    }

    static class TestRepository extends ABaseRepository<TestEntity> {}

    @ParameterizedTest
    @MethodSource("provideForSnake")
    void testToSnakeCase(String input, String expected) {
        assertEquals(expected, RepositoryUtils.toSnakeCase(input));
    }

    private static Stream<Arguments> provideForSnake() {
        return Stream.of(Arguments.of("movieTitle", "movie_title"),
                Arguments.of("userId", "user_id"),
                Arguments.of("createdAt", "created_at"));
    }

    @Test
    void testGetParameters() {
        List<String> params = RepositoryUtils.getParameters(TestEntity.class);
        assertEquals(List.of("name", "age", "created_at", "genre_id"), params);
    }

    @Test
    void testGetTableName() {
        assertEquals("genres", RepositoryUtils.getTableName(Genre.class));
        assertEquals("movies", RepositoryUtils.getTableName(Movie.class));
    }

    @ParameterizedTest
    @MethodSource("provideClassesForEntityName")
    void testGetEntityName(Class<?> clazz, String entityName) {
        assertEquals(entityName, RepositoryUtils.getEntityName(clazz));
    }

    private static Stream<Arguments> provideClassesForEntityName() {
        return Stream.of(Arguments.of(Movie.class, "movie"), Arguments.of(Genre.class, "genre"));
    }

    @ParameterizedTest
    @MethodSource("provideClassesForId")
    void testGetForeignKeyName(Class<?> clazz, String foreignKey) {
        assertEquals(foreignKey, RepositoryUtils.getForeignKeyName(clazz));
    }

    private static Stream<Arguments> provideClassesForId() {
        return Stream.of(Arguments.of(Genre.class, "genre_id"), Arguments.of(Movie.class, "movie_id"));
    }

    @ParameterizedTest
    @MethodSource("provideClasses")
    void testGetRelationTableName(Class<?> clazz, String tableName) {
        assertEquals(tableName, RepositoryUtils.getRelationTableName(clazz, Genre.class));
    }

    private static Stream<Arguments> provideClasses() {
        return Stream.of(Arguments.of(Movie.class, "movie_genres"), Arguments.of(Genre.class, "genre_genres"));
    }

    @Test
    void testGetRelatedEntityClass() throws NoSuchFieldException {
        Field field = TestEntity.class.getDeclaredField("movies");
        assertEquals(Movie.class, RepositoryUtils.getRelatedEntityClassFromList(field));
    }

    @Test
    void testGetEntityClass() throws NoSuchFieldException {
        TestRepository baseRepository = mock(TestRepository.class);

        assertEquals(TestEntity.class, RepositoryUtils.getEntityClassFromRepository(baseRepository.getClass()));
    }

    @Test
    void testSetPreparedStatementParams() throws SQLException, IllegalAccessException, NotValideEntityException {
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        Genre genre = new Genre();
        genre.setId(5);
        TestEntity entity = new TestEntity("John", 30, new Date(), genre);

        RepositoryUtils.setPreparedStatementParams(preparedStatement, entity);

        verify(preparedStatement, times(1)).setObject(1, "John");
        verify(preparedStatement, times(1)).setObject(2, 30);
        verify(preparedStatement, times(1)).setTimestamp(eq(3), any());
        verify(preparedStatement, times(1)).setInt(4, 5);
    }

    @Test
    void testSetPreparedStatementParamsThrowsExceptionForNullFields() {
        TestEntity entity = new TestEntity(null, 30, new Date(), null);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        assertThrows(NotValideEntityException.class, () -> RepositoryUtils.setPreparedStatementParams(preparedStatement, entity));
    }
}

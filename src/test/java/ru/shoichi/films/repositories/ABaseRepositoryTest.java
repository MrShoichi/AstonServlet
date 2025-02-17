package ru.shoichi.films.repositories;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.shoichi.films.config.DBConnector;
import ru.shoichi.films.entities.Actor;
import ru.shoichi.films.entities.Genre;
import ru.shoichi.films.entities.Movie;
import ru.shoichi.films.exceptions.NotFoundRelationsException;
import ru.shoichi.films.exceptions.NotValideEntityException;
import ru.shoichi.films.repositories.impl.MovieRepositoryImpl;
import ru.shoichi.films.repositories.managers.BaseEntityRelationLoader;
import ru.shoichi.films.repositories.managers.BaseRelationManager;
import ru.shoichi.films.repositories.managers.EntityMapper;

import java.sql.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ABaseRepositoryTest {

    @Mock
    private DBConnector dbConnector;

    @Mock
    private BaseRelationManager<Movie> relationManager;

    @Mock
    private EntityMapper<Movie> entityMapper;

    @Mock
    private BaseEntityRelationLoader<Movie> entityRelationLoader;

    @InjectMocks
    private MovieRepositoryImpl baseRepository;


    @Test
    void testFindAllReturnListOfEntities() throws SQLException {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(dbConnector.GetConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(entityMapper.getEntity(any())).thenReturn(new Movie());
        when(resultSet.next()).thenReturn(true, true, true, false);

        List<Movie> movies = baseRepository.findAll();

        assertNotNull(movies);
        verify(statement).executeQuery(anyString());
    }

    @Test
    void testFindByIdReturnEntity() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(dbConnector.GetConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(entityMapper.getEntity(resultSet)).thenReturn(new Movie());

        Movie movie = baseRepository.findById(1);

        assertNotNull(movie);
        verify(statement, times(1)).setInt(1, 1);
    }

    @Test
    void testFindByIdReturnNullWhenEntityNotFound() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(dbConnector.GetConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Movie movie = baseRepository.findById(1);

        assertNull(movie);
    }


    @Test
    void testSaveEntity() throws SQLException, NotValideEntityException, NotFoundRelationsException, IllegalAccessException {
        Movie movie = getMovie();


        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet generatedKeys = mock(ResultSet.class);

        when(dbConnector.GetConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(1);
        doNothing().when(relationManager).saveRelation(any(), any());

        doNothing().when(entityRelationLoader).loadRelation(any());
        Movie savedMovie = baseRepository.save(movie);

        assertNotNull(savedMovie);
        assertEquals(1, savedMovie.getId());
    }

    @SneakyThrows
    @Test
    void testSaveEntityWithException() {
        Movie movie = getMovie();


        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        when(dbConnector.GetConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException());

        try {
            baseRepository.save(movie);
        } catch (Exception ignored) {}

        verify(connection, times(1)).rollback();
    }

    @SneakyThrows
    @Test
    void testSaveEntityWithBatchException() {
        Movie movie = getMovie();


        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        when(dbConnector.GetConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new BatchUpdateException());

        try {
            baseRepository.save(movie);
        } catch (Exception ignored) {}

        verify(connection, times(1)).rollback();
    }

    private static Movie getMovie() {
        Movie movie = new Movie();
        movie.setId(1);
        movie.setDuration(120);
        movie.setTitle("Updated Movie");
        movie.setDescription("Updated description");
        movie.setCreatedAt(new Date());

        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Action");
        movie.setGenres(Collections.singletonList(genre));

        Actor actor = new Actor();
        actor.setId(1);
        actor.setName("John Doe");
        movie.setActors(Collections.singletonList(actor));

        movie.setRating(4.5);
        return movie;
    }

    @Test
    void testUpdateEntity() throws SQLException {
        Movie movie = getMovie();

        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        when(dbConnector.GetConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        Movie updatedMovie = baseRepository.update(movie);

        assertNotNull(updatedMovie);
        assertEquals(1, updatedMovie.getId());
    }

    @Test
    void testUpdateEntityWithSqlException() throws SQLException {
        Movie movie = getMovie();
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(dbConnector.GetConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException());
        try {
            baseRepository.update(movie);
        } catch (Exception ignored) {}

        verify(connection, times(1)).rollback();
    }

    @Test
    void testDeleteEntityWithRelations() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);
        Movie movie = new Movie();
        movie.setId(1);
        Genre genre = new Genre();
        genre.setId(1);
        movie.setGenres(Collections.singletonList(genre));


        when(dbConnector.GetConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);


        doNothing().when(relationManager).deleteAllLastRelations(any(), any());

        boolean deleted = baseRepository.delete(1);

        assertTrue(deleted);
        verify(preparedStatement, times(2)).setInt(1, 1);
        verify(relationManager).deleteAllLastRelations(any(), any());
    }


    @Test
    void testDeleteEntityWithoutRelations() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);
        when(dbConnector.GetConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        boolean deleted = baseRepository.delete(1);

        assertTrue(deleted);
        verify(preparedStatement, times(2)).setInt(1, 1);
        verify(relationManager, times(1)).deleteAllLastRelations(any(), any());
    }

    @Test
    void testDeleteEntityWithSqlException() throws SQLException {
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(dbConnector.GetConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException());
        try {
            boolean deleted = baseRepository.delete(1);
            assertFalse(deleted);
        } catch (Exception ignored) {}

        verify(preparedStatement, times(1)).setInt(1, 1);
        verify(connection, times(1)).rollback();
    }
}

package ru.shoichi.films.repositories.managers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.shoichi.films.config.DBConnector;
import ru.shoichi.films.entities.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntityRelationLoaderTest {

    @Mock
    private DBConnector dbConnector;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private EntityMapper<Movie> entityMapper;

    @InjectMocks
    private EntityRelationLoader<Movie> entityRelationLoader;

    @InjectMocks
    private EntityRelationLoader<User> entityRelationLoaderWithSingleRelation;

    @BeforeEach
    void setUp() throws Exception {
        entityRelationLoader = spy(new EntityRelationLoader<>(dbConnector, Movie.class));
        entityRelationLoaderWithSingleRelation = spy(new EntityRelationLoader<>(dbConnector, User.class));
        entityRelationLoader.setEntityMapper(entityMapper);
    }

    @Test
    void testLoadRelation_WithListField() throws Exception {
        Movie testMovie = getMovie();
        testMovie.setGenres(List.of(new Genre()));
        testMovie.setActors(List.of(new Actor()));

        doNothing().when(entityRelationLoader).loadRelatedEntities(any(), any(), any());

        entityRelationLoader.loadRelation(testMovie);


        verify(entityRelationLoader, times(2)).loadRelatedEntities(any(), any(), any());
    }

    private static Movie getMovie() {
        Movie testMovie = new Movie();
        testMovie.setId(1);
        return testMovie;
    }

    @Test
    void testLoadRelation_WithSingleEntityField() throws Exception {
        User testUser = new User();
        Role role = new Role();
        role.setId(4);
        testUser.setRole(role);

        doReturn(role).when(entityRelationLoaderWithSingleRelation)
                .getRelatedEntity(4, Role.class);

        entityRelationLoaderWithSingleRelation.loadRelation(testUser);

        Role loadedDirector = testUser.getRole();
        assertNotNull(loadedDirector);
        assertEquals(4, loadedDirector.getId());

        verify(entityRelationLoaderWithSingleRelation, times(1)).getRelatedEntity(eq(4), eq(Role.class));
    }


    @Test
    void testGetRelatedEntities() throws Exception {
        when(resultSet.next()).thenReturn(true, false);
        Genre genre = new Genre();
        genre.setId(1);
        when(entityMapper.getEntity(any(ResultSet.class), any())).thenReturn(genre);
        when(dbConnector.GetConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        List<Genre> relatedMovies = entityRelationLoader.getRelatedEntities(1, Genre.class);

        assertNotNull(relatedMovies);
        assertEquals(1, relatedMovies.size());
        verify(preparedStatement).setInt(1, 1);
        verify(preparedStatement).executeQuery();
    }

    @Test
    void testGetRelatedEntity() throws Exception {
        when(resultSet.next()).thenReturn(true);
        Movie movie = new Movie();
        movie.setId(2);
        when(entityMapper.getEntity(any(ResultSet.class), any(Movie.class), eq(true))).thenReturn(movie);
        when(dbConnector.GetConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        Movie relatedMovie = (Movie) entityRelationLoader.getRelatedEntity(2, Movie.class);

        assertNotNull(relatedMovie);
        assertEquals(2, relatedMovie.getId());
        verify(preparedStatement).setInt(1, 2);
        verify(preparedStatement).executeQuery();
    }

    @Test
    void testGetRelatedEntity_NotFound() throws Exception {
        when(resultSet.next()).thenReturn(false);
        when(dbConnector.GetConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        Object relatedEntity = entityRelationLoader.getRelatedEntity(3, Movie.class);

        assertNull(relatedEntity);
        verify(preparedStatement).setInt(1, 3);
        verify(preparedStatement).executeQuery();
    }

    @Test
    void testLoadRelatedEntities() throws Exception {
        when(resultSet.next()).thenReturn(true, false);
        Movie movie = new Movie();
        movie.setId(4);
        when(entityMapper.getEntity(any(ResultSet.class), any(Movie.class))).thenReturn(movie);
        when(dbConnector.GetConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        List<Movie> movies = List.of(movie);
        List<BaseEntity> loadedMovies = new java.util.ArrayList<>();

        entityRelationLoader.loadRelatedEntities(Movie.class, movies, loadedMovies);

        assertEquals(1, loadedMovies.size());
        verify(preparedStatement).executeQuery();
    }
}

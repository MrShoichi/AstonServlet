package ru.shoichi.films.repositories.managers;

import static org.mockito.Mockito.*;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.shoichi.films.entities.BaseEntity;
import ru.shoichi.films.utils.SQLQueryBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RelationManagerTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @InjectMocks
    private RelationManager<BaseEntity> relationManager;

    private BaseEntity entity;

    @BeforeEach
    public void setUp() throws SQLException {
        relationManager = spy(relationManager);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        BaseEntity relatedEntity = new RelatedEntity();
        relatedEntity.setId(1);
        entity = new TestEntity(List.of(relatedEntity));
        entity.setId(2);
    }

    @Test
    public void testSaveRelationWithManyRelation() throws SQLException {

        doNothing().when(preparedStatement).addBatch();
        when(preparedStatement.executeBatch()).thenReturn(new int[0]);
        doNothing().when(preparedStatement).setInt(anyInt(), anyInt());
        when(preparedStatement.executeUpdate()).thenReturn(1);

        relationManager.saveRelation(entity, connection);

        verify(connection, times(2)).prepareStatement(anyString());
        verify(preparedStatement, times(1)).addBatch();
        verify(preparedStatement, times(1)).executeBatch();
    }

    @Test
    public void testDeleteAllLastRelations() throws SQLException {
        relationManager.deleteAllLastRelations(entity, connection);
        verify(connection, times(1)).prepareStatement(anyString());
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @SneakyThrows
    @Test
    public void testDeleteLastRelations() {
        Method method = relationManager.getClass().getDeclaredMethod("deleteLastRelations", BaseEntity.class, String.class, Connection.class);
        method.setAccessible(true);
        method.invoke(relationManager, entity, "relation_table", connection);
        verify(connection, times(1)).prepareStatement(anyString());
        verify(preparedStatement, times(1)).setInt(1, entity.getId());
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @AllArgsConstructor
    private static class TestEntity extends BaseEntity {
        private List<BaseEntity> relatedEntities;
    }

    @AllArgsConstructor
    private static class RelatedEntity extends BaseEntity {
    }
}

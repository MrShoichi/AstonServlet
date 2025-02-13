package ru.shoichi.films.repositories.managers;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.shoichi.films.entities.BaseEntity;
import ru.shoichi.films.exceptions.CyclicException;
import ru.shoichi.films.exceptions.UnsupportedFieldException;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class EntityMapperTest {

    @Mock
    private BaseEntityRelationLoader<TestEntity> relationLoader;
    @Mock
    private BaseEntityRelationLoader<EntityWithCyclicRelation> relationLoaderWithCyclicException;

    private EntityMapper<TestEntity> entityMapper;
    private EntityMapper<EntityWithCyclicRelation> cyclicEntityMapper;

    @BeforeEach
    void setUp() {
        entityMapper = new EntityMapper<>(relationLoader, TestEntity.class);
        cyclicEntityMapper = new EntityMapper<>(relationLoaderWithCyclicException, EntityWithCyclicRelation.class);
    }

    @SneakyThrows
    @Test
    void testGetEntityWithStringField() {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString("name")).thenReturn("Test Name");
        when(resultSet.getInt("id")).thenReturn(1);



        var entity = new EntityWithString();


        entityMapper.getEntity(resultSet, entity);
        Field nameField = EntityWithString.class.getDeclaredField("name");
        nameField.setAccessible(true);
        assertEquals("Test Name", nameField.get(entity));
    }

    @SneakyThrows
    @Test
    void testGetEntityWithIntegerField() {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt("age")).thenReturn(30);

        TestEntity entity = new TestEntity();
        Field ageField = TestEntity.class.getDeclaredField("age");
        ageField.setAccessible(true);

        entityMapper.getEntity(resultSet, entity);

        assertEquals(30, ageField.get(entity));
    }

    @SneakyThrows
    @Test
    void testGetEntityWithDateField() {
        ResultSet resultSet = mock(ResultSet.class);
        Date testDate = new Date();
        when(resultSet.getDate("created_at")).thenReturn(new java.sql.Date(testDate.getTime()));

        TestEntity entity = new TestEntity();
        Field dateField = TestEntity.class.getDeclaredField("createdAt");
        dateField.setAccessible(true);

        entityMapper.getEntity(resultSet, entity);

        assertEquals(testDate, dateField.get(entity));
    }

    @SneakyThrows
    @Test
    void testGetEntityWithListField() {
        ResultSet resultSet = mock(ResultSet.class);
        List<EntityWithString> mockRelatedEntities = Arrays.asList(new EntityWithString(), new EntityWithString());
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getInt("age")).thenReturn(17);
        when(resultSet.getDate("created_at")).thenReturn(new java.sql.Date(new Date().getTime()));

        when(relationLoader.getRelatedEntities(anyInt(), eq(EntityWithString.class))).thenReturn(mockRelatedEntities);

        TestEntity entity = new TestEntity();
        Field listField = TestEntity.class.getDeclaredField("relatedEntities");
        listField.setAccessible(true);

        entityMapper.getEntity(resultSet, entity);

        assertEquals(mockRelatedEntities, listField.get(entity));
    }

    @SneakyThrows
    @Test
    void testGetEntityWithCyclicRelation() {
        ResultSet resultSet = mock(ResultSet.class);

        EntityWithCyclicRelation entity = new EntityWithCyclicRelation();
        assertThrows(CyclicException.class, () -> cyclicEntityMapper.getEntity(resultSet, entity));
    }

    @SneakyThrows
    @Test
    void testGetEntityWithUnsupportedFieldType() {
        ResultSet resultSet = mock(ResultSet.class);

        EntityWithUnsupported entity = new EntityWithUnsupported();
        assertThrows(UnsupportedFieldException.class, () -> entityMapper.getEntity(resultSet, entity));
    }

    @SneakyThrows
    @Test
    void testGetEntityWithJsonIgnoreField() {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString("name")).thenReturn("Test Name");

        BaseEntityWithJsonIgnore entity = new BaseEntityWithJsonIgnore();
        Field nameField = BaseEntityWithJsonIgnore.class.getDeclaredField("entity");
        nameField.setAccessible(true);

        entity = entityMapper.getEntity(resultSet, entity);

        assertNull(nameField.get(entity));
    }

    @SneakyThrows
    @Test
    void testGetEntityWithJsonBackReferenceField() {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getInt("age")).thenReturn(17);
        when(resultSet.getDate("created_at")).thenReturn(new java.sql.Date(new Date().getTime()));
        when(resultSet.getInt("base_entity_with_relation_id")).thenReturn(1);
        TestEntity entity = new TestEntity();
        BaseEntityWithRelation baseEntityWithRelation = new BaseEntityWithRelation();
        Field nameField = TestEntity.class.getDeclaredField("entity");
        nameField.setAccessible(true);
        when(relationLoader.getRelatedEntity(anyInt(), any())).thenReturn(baseEntityWithRelation);
        entity = entityMapper.getEntity(resultSet, entity);

        assertNotNull(nameField.get(entity));
    }

    static class TestEntity extends BaseEntity {
        @JsonIgnore
        private String name;
        private int age;
        private Date createdAt;
        private List<EntityWithString> relatedEntities;
        private BaseEntityWithRelation entity;
    }

    static class BaseEntityWithJsonIgnore {
        private String name;
        @JsonIgnore
        EntityWithString entity;
    }

    static class EntityWithCyclicRelation extends BaseEntity {
        EntityWithCyclicRelation testEntity;
    }

    static class BaseEntityWithRelation extends BaseEntity {
        @JsonBackReference
        TestEntity testEntity;
    }

    static class EntityWithString extends BaseEntity {
        private String name;
    }

    static class EntityWithUnsupported extends BaseEntity {
        private int[] age;
    }


}

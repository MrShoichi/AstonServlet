package ru.shoichi.films.services;

import ru.shoichi.films.entities.BaseEntity;
import ru.shoichi.films.exceptions.NoEntityException;
import ru.shoichi.films.exceptions.NotFoundRelationsException;
import ru.shoichi.films.exceptions.NotValideEntityException;
import ru.shoichi.films.mappers.BaseMapper;
import ru.shoichi.films.repositories.BaseRepository;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ABaseService<T extends BaseEntity, D> implements BaseService<D> {
    protected final BaseRepository<T> repository;
    protected final BaseMapper<T, D> mapper;

    public ABaseService(BaseRepository<T> repository, BaseMapper<T, D> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<D> getAll() {
        List<T> entities = repository.findAll();
        return entities.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

    }

    @Override
    public D getById(Integer id) {
        T entity = repository.findById(id);
        return entity != null ? mapper.toDto(entity) : null;
    }

    @Override
    public D save(D dto) throws NotFoundRelationsException, NotValideEntityException {
        T entity = mapper.fromDto(dto);
        entity = repository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    public D update(D dto) throws NoEntityException {

        T entity = mapper.fromDto(dto);
        if(repository.findById(entity.getId()) == null) {
            throw new NoEntityException("Нет записи с таким Id: " + entity.getId());
        }
        entity = repository.update(entity);
        return mapper.toDto(entity);

    }

    @Override
    public boolean delete(Integer id) throws NoEntityException {
        if(repository.findById(id) == null) {
            throw new NoEntityException("Нет записи с таким Id: " + id);
        }
        return repository.delete(id);
    }

}

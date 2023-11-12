package fr.perso.springserie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.repository.IBaseRepo;
import fr.perso.springserie.service.interfaces.IBaseService;
import fr.perso.springserie.service.interfaces.IMapper;
import fr.perso.springserie.task.MapService;
import jakarta.persistence.Embedded;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class BaseService<E extends BaseEntity, D extends BaseDTO> implements IBaseService<D> {

    protected final IBaseRepo<E> repository;
    protected final ModelMapper mapper;
    protected final Class<D> dtoClass;
    protected final Class<E> entityClass;

    protected final MapService mapService;
    protected final IMapper<E, D> customMapper;

    protected BaseService(IBaseRepo<E> repository, Class<D> dtoClass, Class<E> entityClass, MapService mapService, IMapper<E, D> customMapper) {
        this.repository = repository;
        this.dtoClass = dtoClass;
        this.entityClass = entityClass;
        this.mapService = mapService;
        this.customMapper = customMapper;
        mapper = new ModelMapper();
    }

    @Override
    public List<D> getAll() {
        return customMapper.toDTOList(repository.findAll(), dtoClass);
    }

    @Override
    public D getById(int id) {
        return customMapper.toDTO(repository.findById(id).orElse(null), dtoClass, entityClass);
    }

    @Override
    public List<D> getBydIds(List<Integer> ids) {
        return customMapper.toDTOList(repository.findByIdIn(ids), dtoClass);
    }

    @Override
    public List<D> search(D dto) {
        final ExampleMatcher[] exampleMatcher = {ExampleMatcher.matchingAny()
                .withIgnoreNullValues().withIgnorePaths("id")};

        customMapper.browseField(entityClass, customMapper.toEntity(dto, entityClass), (field, entity) -> {
            if (field.getAnnotation(Embedded.class) != null) {
                field.setAccessible(true);
                try {

                    customMapper.browseField(field.getType(), field.get(entity), (embeddedField, object) -> {
                    });
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                field.setAccessible(false);
            }
            exampleMatcher[0] = exampleMatcher[0].withMatcher(field.getName(), matcher -> matcher.contains().ignoreCase());
        });
        List<E> entities = repository.findAll(Example.of(customMapper.toEntity(dto, entityClass), exampleMatcher[0]));
        return customMapper.toDTOList(entities, dtoClass);
    }

    @Override
    public void save(D d) {
        repository.save(customMapper.toEntity(d, entityClass));
    }

    @Override
    public void save(File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            List<D> dtoList = objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, dtoClass));
            dtoList.forEach(d -> {
                if(search(d)==null)
                    this.save(d);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        repository.delete(customMapper.toEntity(getById(id), entityClass));
    }


}

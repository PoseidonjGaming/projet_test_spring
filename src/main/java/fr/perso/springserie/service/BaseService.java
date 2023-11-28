package fr.perso.springserie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.repository.IBaseRepo;
import fr.perso.springserie.service.interfaces.IBaseService;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.task.MapService;
import jakarta.persistence.Embedded;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static fr.perso.springserie.service.utility.ServiceUtility.browseField;

public abstract class BaseService<E extends BaseEntity, D extends BaseDTO> implements IBaseService<D> {

    protected final IBaseRepo<E> repository;
    protected final ModelMapper mapper;
    protected final Class<D> dtoClass;
    protected final Class<E> entityClass;

    protected final MapService mapService;
    protected final IMapper customMapper;

    protected BaseService(IBaseRepo<E> repository, Class<D> dtoClass, Class<E> entityClass, MapService mapService, IMapper customMapper) {
        this.repository = repository;
        this.dtoClass = dtoClass;
        this.entityClass = entityClass;
        this.mapService = mapService;
        this.customMapper = customMapper;
        mapper = new ModelMapper();
    }

    @Override
    public List<D> getAll() {
        return repository.findAll().stream().map(e -> customMapper.convert(e, dtoClass)).toList();
    }

    @Override
    public D getById(int id) {
        return customMapper.convert(repository.findById(id).orElse(null), dtoClass);
    }

    @Override
    public List<D> getBydIds(List<Integer> ids) {
        return customMapper.convertList(repository.findByIdIn(ids), dtoClass);
    }

    @Override
    public List<D> search(D dto, ExampleMatcher.MatchMode mode, ExampleMatcher.StringMatcher matcherType) {
        return customMapper.convertList(repository.findAll(Example.of(customMapper.convert(dto, entityClass),
                getMatcher(dto, mode, matcherType))), dtoClass);
    }

    @NotNull
    protected ExampleMatcher getMatcher(D dto, ExampleMatcher.MatchMode mode, ExampleMatcher.StringMatcher matcherType) {
        final ExampleMatcher[] exampleMatcher = new ExampleMatcher[1];
        if (mode.equals(ExampleMatcher.MatchMode.ALL)) {
            exampleMatcher[0] = ExampleMatcher.matchingAll()
                    .withIgnoreNullValues().withIgnorePaths("id");
        } else {
            exampleMatcher[0] = ExampleMatcher.matchingAny()
                    .withIgnoreNullValues().withIgnorePaths("id");
        }


        browseField(entityClass, customMapper.convert(dto, entityClass), (field, entity) -> {
            if (field.getAnnotation(Embedded.class) != null) {
                browseField(field.getType(), embeddedField ->
                        exampleMatcher[0] = exampleMatcher[0].withMatcher(getPath(field.getName(), embeddedField.getName()), matcher ->
                                matcher.stringMatcher(matcherType)));

            }
            exampleMatcher[0] = exampleMatcher[0].withMatcher(field.getName(), matcher -> matcher.stringMatcher(matcherType));
        });
        return exampleMatcher[0];
    }

    private String getPath(String... parts) {
        return Arrays.stream(parts).reduce((s, s2) -> s + "." + s2).orElse("");
    }

    @Override
    public D save(D d) {
        return customMapper.convert(repository.save(customMapper.convert(d, entityClass)), dtoClass);
    }

    @Override
    public void save(File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            saves(objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, dtoClass)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saves(List<D> ds) {
        List<D> list = ds.stream()
                .filter(dto -> !search(dto, ExampleMatcher.MatchMode.ALL, ExampleMatcher.StringMatcher.EXACT).isEmpty()).toList();
        if (list.isEmpty()) {
            repository.saveAll(customMapper.convertList(ds, entityClass));
        }
    }

    @Override
    public void delete(int id) {
        repository.delete(customMapper.convert(getById(id), entityClass));
    }

    @Override
    public List<D> order(String field, Sort.Direction direction) {
        return customMapper.convertList(repository.findAll(Sort.by(direction, field)), dtoClass);
    }
}

package fr.perso.springserie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.perso.springserie.model.PageRequest;
import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.dto.special.SearchDateDto;
import fr.perso.springserie.model.dto.special.SearchDto;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.repository.IBaseRepo;
import fr.perso.springserie.service.interfaces.IBaseService;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.task.MapService;
import jakarta.persistence.Embedded;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
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

    protected static boolean isBetween(SearchDateDto searchDateDto, LocalDate releaseDate) {
        return (releaseDate.isEqual(searchDateDto.getStartDate()) || releaseDate.isEqual(searchDateDto.getEndDate()))
                || (releaseDate.isAfter(searchDateDto.getStartDate()) && releaseDate.isBefore(searchDateDto.getEndDate()));
    }

    @Override
    public PageRequest<D> getAll() {
        return getAll(0, 0);
    }

    @Override
    public PageRequest<D> getAll(int size, int page) {
        return createPage(repository.findAll((size == 0) ? Pageable.unpaged() : Pageable.ofSize(size).withPage(page)));
    }

    private PageRequest<D> createPage(Page<E> pageRequest) {
        return new PageRequest<>(customMapper.convertList(pageRequest.getContent(), dtoClass), pageRequest.getNumber(), pageRequest.getTotalElements());
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
    public List<D> search(D dto, SearchDto searchDto, SearchDateDto searchDateDto) {
        List<E> entities = repository.findAll(Example.of(customMapper.convert(dto, entityClass),
                getMatcher(dto, searchDto.getMode(), searchDto.getType())));
        return customMapper.convertList(entities, dtoClass);
    }

    @Override
    public List<D> sort(String field, Sort.Direction direction) {
        return customMapper.convertList(repository.findAll(Sort.by(direction, field)), dtoClass);
    }

    @Override
    public List<D> sortSearch(String field, Sort.Direction direction, D dto, ExampleMatcher.MatchMode mode, ExampleMatcher.StringMatcher matcherType) {
        return customMapper.convertList(repository.findAll(
                Example.of(customMapper.convert(dto, entityClass), getMatcher(dto, mode, matcherType)), Sort.by(direction, field)
        ), dtoClass);
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
                .filter(dto -> !search(dto, new SearchDto(ExampleMatcher.MatchMode.ALL, ExampleMatcher.StringMatcher.EXACT), new SearchDateDto()).isEmpty()).toList();
        if (list.isEmpty()) {
            repository.saveAll(customMapper.convertList(ds, entityClass));
        }
    }

    @Override
    public void delete(int id) {
        repository.delete(customMapper.convert(getById(id), entityClass));
    }


}

package fr.perso.springserie.service.imp.crud;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.perso.springserie.interceptor.exception.GenericException;
import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.dto.PagedResponse;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.model.dto.special.SortDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.repository.IBaseRepository;
import fr.perso.springserie.service.interfaces.crud.IBaseService;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.utility.annotation.Json;
import org.springframework.data.domain.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static fr.perso.springserie.service.utility.SearchUtility.*;
import static fr.perso.springserie.service.utility.ServiceUtility.getMap;

public abstract class BaseService<E extends BaseEntity, D extends BaseDTO> implements IBaseService<D> {

    protected final IBaseRepository<E> repository;
    protected final IMapper mapper;
    protected final Class<D> dtoClass;
    protected final Class<E> entityClass;

    private final E entity;

    protected BaseService(IBaseRepository<E> repository, IMapper mapper, Class<D> dtoClass, Class<E> entityClass) {
        this.repository = repository;
        this.mapper = mapper;
        this.dtoClass = dtoClass;
        this.entityClass = entityClass;
        try {
            entity = entityClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new GenericException(e);
        }
    }

    private static Pageable getPageable(int size, int page) {
        return Pageable.ofSize(size).withPage(page);
    }

    private PagedResponse<D> createPage(Page<E> pageRequest, SearchDTO<D> searchDTO) {
        List<D> list = mapper.convertList(pageRequest.getContent(), dtoClass);
        if (searchDTO != null) {
            list = list.stream().filter(d -> filtering(d, searchDTO)).toList();
        }

        return new PagedResponse<>(list, pageRequest.getTotalElements());
    }

    @Override
    public PagedResponse<D> getAll(int size, int page) {
        return createPage(repository.findAll(Example.of(entity), getPageable(size, page)), null);
    }

    @Override
    public List<D> getByIds(List<String> ids) {
        return mapper.convertList(repository.findByIdIn(ids, Example.of(entity)), dtoClass);
    }

    @Override
    public PagedResponse<D> getByIds(List<String> ids, int size, int page) {
        return createPage(repository.findByIdIn(ids, Example.of(entity), getPageable(size, page)), null);
    }

    @Override
    public PagedResponse<D> search(SearchDTO<D> searchDto, int size, int page) {
        return createPage(repository.findAll(Example.of(
                        mapper.convert(searchDto.getDto(), entityClass),
                        getMatcher(searchDto.getMode(), searchDto.getType(), entityClass)),
                getPageable(size, page)
        ), searchDto);
    }

    @Override
    public PagedResponse<D> sort(SortDTO sortDTO, int size, int page) {
        return createPage(repository.findAll(Example.of(entity), PageRequest.of(page, size, sortDTO.getDirection(),
                getPath(findField(entityClass, sortDTO.getField()).toArray(new String[]{})))), null);
    }

    @Override
    public PagedResponse<D> sortSearch(SearchDTO<D> searchDto, SortDTO sortDTO, int size, int pageNumber) {
        List<D> temp = sortSearch(searchDto, sortDTO);
        List<D> response = temp.stream().skip((long) size * pageNumber).limit(size).toList();
        return new PagedResponse<>(response, temp.size());
    }

    @Override
    public Map<String, String> getStructure() {
        Map<String, String> structure = new LinkedHashMap<>();
        Class<?> superClass = dtoClass.getSuperclass();

        BiConsumer<Field, Map<String, String>> consumer = (field, map) -> {
            if (field.getType().getSuperclass().equals(Number.class)) {
                map.put(field.getName(), "number");
            } else if (field.getType().equals(LocalDate.class)) {
                map.put(field.getName(), "date");
            } else if (field.isAnnotationPresent(Json.class)) {
                map.put(field.getName(), field.getAnnotation(Json.class).type());
            } else {
                map.put(field.getName(), field.getType().getSimpleName().toLowerCase());
            }
        };

        while (!superClass.equals(Object.class)) {
            structure.putAll(getMap(superClass, consumer));
            superClass = superClass.getSuperclass();
        }

        structure.putAll(getMap(dtoClass, consumer));

        return structure;
    }


    @Override
    public Map<String, String> getTypes() {
        Map<String, String> types = new LinkedHashMap<>();
        Class<?> superClass = entityClass.getSuperclass();

        BiConsumer<Field, Map<String, String>> biConsumer = (field, map) -> {
            if (field.isAnnotationPresent(Json.class)) {
                map.put(field.getName(), field.getAnnotation(Json.class).type());
            } else {
                map.put(field.getName(), field.getType().getSimpleName());
            }
        };

        while (!superClass.equals(Object.class)) {
            types.putAll(getMap(superClass, biConsumer));
            superClass = superClass.getSuperclass();
        }

        types.putAll(getMap(dtoClass, biConsumer));
        return types;
    }

    @Override
    public Map<String, String> getDisplay() {
        Map<String, String> display = getMap(dtoClass, (field, map) -> {
            if (!map.containsKey(field.getType().getSimpleName()) && field.isAnnotationPresent(Json.class)) {
                map.put(field.getName(), field.getAnnotation(Json.class).display());
            }
        });
        display.put("current", entityClass.getAnnotation(Json.class).display());
        return display;
    }

    @Override
    public List<D> getAll() {
        return mapper.convertList(repository.findAll(Example.of(entity)), dtoClass);
    }

    @Override
    public List<D> search(SearchDTO<D> searchDto) {
        return mapper.convertList(repository.findAll(
                Example.of(
                        mapper.convert(searchDto.getDto(), entityClass),
                        getMatcher(searchDto.getMode(), searchDto.getType(), entityClass)
                )), dtoClass).stream().filter(dto -> filtering(dto, searchDto)).toList();
    }


    @Override
    public List<D> sort(SortDTO sortDTO) {
        return mapper.convertList(repository.findAll(Example.of(entity), Sort.by(sortDTO.getDirection(),
                getPath(findField(entityClass, sortDTO.getField()).toArray(new String[]{})))), dtoClass);

    }

    @Override
    public List<D> sortSearch(SearchDTO<D> searchDto, SortDTO sortDTO) {
        return mapper.convertList(repository.findAll(
                        Example.of(mapper.convert(searchDto.getDto(), entityClass),
                                getMatcher(searchDto.getMode(), searchDto.getType(), entityClass)),
                        Sort.by(sortDTO.getDirection(),
                                getPath(findField(entityClass, sortDTO.getField()).toArray(new String[]{}))
                        )
                ),
                dtoClass
        ).stream().filter(dto -> filtering(dto, searchDto)).toList();
    }

    @Override
    public D getById(String id) {
        return repository.findById(id).map(e -> mapper.convert(e, dtoClass)).orElse(null);
    }

    @Override
    public D save(D dto) {
        return mapper.convert(repository.save(mapper.convert(dto, entityClass)), dtoClass);
    }

    @Override
    public void save(File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            saves(objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, dtoClass)));
        } catch (IOException e) {
            throw new GenericException(e);
        }
    }

    @Override
    public void saves(List<D> ds) {
        List<D> list = ds.stream()
                .filter(dto -> !search(new SearchDTO<>(dto,
                        ExampleMatcher.MatchMode.ALL,
                        ExampleMatcher.StringMatcher.EXACT,
                        null, null)).isEmpty()).toList();
        if (list.isEmpty()) {
            repository.saveAll(mapper.convertList(ds, entityClass));
        }
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}

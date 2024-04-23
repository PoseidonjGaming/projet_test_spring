package fr.perso.springserie.service.imp.crud;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.perso.springserie.interceptor.exception.GenericException;
import fr.perso.springserie.model.JsonType;
import fr.perso.springserie.model.PagedResponse;
import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.model.dto.special.SortDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.repository.IBaseRepo;
import fr.perso.springserie.service.interfaces.crud.IBaseService;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.task.MapService;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import org.springframework.data.domain.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.util.*;
import java.util.function.BiConsumer;

import static fr.perso.springserie.service.utility.SearchUtility.*;
import static fr.perso.springserie.service.utility.ServiceUtility.getMap;

public abstract class BaseService<E extends BaseEntity, D extends BaseDTO> implements IBaseService<D> {

    protected final IBaseRepo<E> repository;
    protected final IMapper mapper;
    protected final Class<D> dtoClass;
    protected final Class<E> entityClass;
    protected final MapService mapService;


    protected BaseService(IBaseRepo<E> repository, IMapper mapper, Class<D> dtoClass, Class<E> entityClass, MapService mapService) {
        this.repository = repository;
        this.mapper = mapper;
        this.dtoClass = dtoClass;
        this.entityClass = entityClass;
        this.mapService = mapService;
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
        return createPage(repository.findAll(getPageable(size, page)), null);
    }

    @Override
    public List<D> getByIds(List<Integer> ids) {
        return mapper.convertList(repository.findByIdIn(ids), dtoClass);
    }

    @Override
    public PagedResponse<D> getByIds(List<Integer> ids, int size, int page) {
        return createPage(repository.findByIdIn(ids, getPageable(size, page)), null);
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
        return createPage(repository.findAll(PageRequest.of(page, size, sortDTO.getDirection(),
                getPath(findField(entityClass, sortDTO.getField())
                        .toArray(new String[]{})))), null);
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
        List<Field> fields = getEntityField(entityClass);

        BiConsumer<Field, Map<String, String>> consumer = (field, map) -> {
            if (field.isAnnotationPresent(JsonType.class)) {
                structure.put(field.getName(), field.getAnnotation(JsonType.class).type());
            } else {
                if (field.getName().endsWith("Id") && field.getType().equals(Integer.class)) {
                    structure.put(field.getName(), "foreign_id");
                } else if (field.getName().endsWith("Ids")) {
                    mappedBy(field, fields, structure);

                } else if (field.getType().equals(LocalDate.class)) {
                    structure.put(field.getName(), "date");
                } else {
                    structure.put(field.getName(), field.getType().getSimpleName());
                }
            }
        };

        while (!superClass.equals(Object.class)) {
            structure.putAll(getMap(superClass, consumer));
            superClass = superClass.getSuperclass();
        }

        structure.putAll(getMap(dtoClass, consumer));

        return structure;
    }

    private static void mappedBy(Field field, List<Field> fields, Map<String, String> structure) {
        fields.stream().filter(entityField ->
                        entityField.getName().equals(field.getName().replace("Ids", "")))
                .findFirst().ifPresent(entityField -> {
                    if (entityField.isAnnotationPresent(ManyToMany.class) &&
                            entityField.getAnnotation(ManyToMany.class).mappedBy().isBlank()) {
                        structure.put(field.getName(), "ids");
                    }
                });
    }

    protected List<Field> getEntityField(Class<?> entityClass) {
        List<Field> fields = new ArrayList<>();
        Arrays.stream(entityClass.getDeclaredFields()).forEach(field -> {
            if (field.isAnnotationPresent(Embedded.class)) {
                fields.addAll(getEntityField(field.getType()));
            } else {
                fields.add(field);
            }
        });
        return fields;
    }


    @Override
    public Map<String, String> getTypes() {
        Map<String, String> types = new LinkedHashMap<>();
        Class<?> superClass = entityClass.getSuperclass();

        BiConsumer<Field, Map<String, String>> biConsumer = (field, map) -> {
            if (field.getType().isAnnotationPresent(Entity.class)) {
                types.put(field.getName().concat("Id"), field.getType().getSimpleName().toLowerCase());
            } else {
                if (field.getType().equals(List.class)) {
                    Class<?> genericType = (Class<?>) ((ParameterizedType) field.getGenericType())
                            .getActualTypeArguments()[0];
                    types.put(field.getName().concat("Ids"), genericType.getSimpleName().toLowerCase());
                } else {
                    types.put(field.getName(), field.getType().getSimpleName().toLowerCase());
                }

            }
        };

        while (!superClass.equals(Object.class)) {
            types.putAll(getMap(superClass, biConsumer));
            superClass = superClass.getSuperclass();
        }

        types.putAll(getMap(entityClass, biConsumer));
        return types;
    }

    @Override
    public Map<String, String> getDisplay() {
        Map<String, String> display = getMap(entityClass, (field, map) -> {
            if (!map.containsKey(field.getType().getSimpleName())) {
                if (field.getType().equals(List.class)) {
                    map.put(field.getName().concat("Ids"),
                            ((Class<?>) ((ParameterizedType) field.getGenericType())
                                    .getActualTypeArguments()[0]).getAnnotation(JsonType.class).display());
                } else if (field.getType().isAnnotationPresent(Entity.class)) {
                    map.put(field.getName().concat("Id"),
                            field.getType().getAnnotation(JsonType.class).display());
                }
            }
        });
        display.put("current", entityClass.getAnnotation(JsonType.class).display());
        return display;
    }

    @Override
    public List<D> getAll() {
        return mapper.convertList(repository.findAll(), dtoClass);
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
        return mapper.convertList(repository.findAll(Sort.by(sortDTO.getDirection(),
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
    public D getById(int id) {
        return repository.findById(id).map(entity -> mapper.convert(entity, dtoClass)).orElse(null);
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
    public void delete(int id) {
        repository.deleteById(id);
    }
}

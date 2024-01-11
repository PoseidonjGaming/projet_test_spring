package fr.perso.springserie.service.imp.crud;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.perso.springserie.model.PagedResponse;
import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.model.dto.special.SortDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.repository.IBaseRepo;
import fr.perso.springserie.service.interfaces.ICRUDService;
import fr.perso.springserie.service.interfaces.listed.IBaseListedService;
import fr.perso.springserie.service.interfaces.paged.IBasePagedService;
import fr.perso.springserie.service.mapper.IMapper;
import fr.perso.springserie.task.MapService;
import jakarta.persistence.Embedded;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static fr.perso.springserie.service.utility.ServiceUtility.*;

public abstract class BaseService<E extends BaseEntity, D extends BaseDTO> implements ICRUDService<D>, IBasePagedService<D>, IBaseListedService<D> {

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

    private static String getPath(String... parts) {
        if (parts[0].isEmpty()) {
            return Arrays.stream(parts).skip(1).reduce((s, s2) -> s + "." + s2).orElse("");
        }
        return Arrays.stream(parts).reduce((s, s2) -> s + "." + s2).orElse("");
    }

    protected ExampleMatcher getMatcher(D dto, ExampleMatcher.MatchMode mode, ExampleMatcher.StringMatcher matcherType) {
        final ExampleMatcher[] exampleMatcher = new ExampleMatcher[1];
        if (mode.equals(ExampleMatcher.MatchMode.ALL)) {
            exampleMatcher[0] = ExampleMatcher.matchingAll()
                    .withIgnoreNullValues().withIgnorePaths("id");
        } else {
            exampleMatcher[0] = ExampleMatcher.matchingAny()
                    .withIgnoreNullValues().withIgnorePaths("id");
        }


        browseField(entityClass, mapper.convert(dto, entityClass), (field, entity) -> {
            if (field.getAnnotation(Embedded.class) != null) {
                browseField(field.getType(), embeddedField ->
                        exampleMatcher[0] = exampleMatcher[0].withMatcher(getPath(field.getName(), embeddedField.getName()), matcher ->
                                matcher.stringMatcher(matcherType).ignoreCase()));

            } else if (field.getType().equals(String.class))
                exampleMatcher[0] = exampleMatcher[0].withMatcher(field.getName(), matcher -> matcher.stringMatcher(matcherType).ignoreCase());
        });
        return exampleMatcher[0];
    }


    protected static boolean isBetween(LocalDate releaseDate, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return (releaseDate.isEqual(startDate) || releaseDate.isEqual(endDate))
                    || (releaseDate.isAfter(startDate) && releaseDate.isBefore(endDate));
        }
        return true;

    }

    protected static <O> boolean filterList(List<O> entityList, List<O> compareTo) {
        if ((entityList != null) && (compareTo != null)) {
            return new HashSet<>(entityList).containsAll(compareTo);
        }
        return false;

    }

    protected static boolean equalsId(int entityId, int searchedId) {
        if (searchedId != 0)
            return entityId == searchedId;
        return true;
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
                        getMatcher(searchDto.getDto(), searchDto.getMode(), searchDto.getType())),
                getPageable(size, page)
        ), searchDto);
    }

    @Override
    public PagedResponse<D> sort(SortDTO sortDTO, int size, int page) {
        try {
            return createPage(repository.findAll(PageRequest.of(page, size, sortDTO.getDirection(),
                    getPath(findField(entityClass.getDeclaredConstructor().newInstance(), sortDTO.getField()).toArray(new String[]{})))), null);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PagedResponse<D> sortSearch(SearchDTO<D> searchDto, SortDTO sortDTO, int size, int pageNumber) {
        List<D> temp = sortSearch(searchDto, sortDTO);
        List<D> response = temp.stream().skip((long) size * pageNumber).limit(size).toList();
        return new PagedResponse<>(response, temp.size());
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
                        getMatcher(searchDto.getDto(), getMode(searchDto, dtoClass), searchDto.getType())
                )), dtoClass).stream().filter(dto -> filtering(dto, searchDto)).toList();
    }

    protected boolean filtering(D dto, SearchDTO<D> searchDto) {
        final boolean[] filtered = {true};
        browseField(dtoClass, field -> {
            if (field.getName().endsWith("Id")) {
                Integer id = get(field, dto);
                Integer searchedId = get(field, searchDto.getDto());
                if(id!=null && searchedId!=null){
                    if (searchDto.getMode().equals(ExampleMatcher.MatchMode.ALL)) {
                        filtered[0] = filtered[0] && equalsId(id, searchedId);
                    } else {
                        filtered[0] = filtered[0] || equalsId(id, searchedId);
                    }
                }

            }


            if (field.getName().endsWith("Ids") && field.getType().equals(List.class)) {
                List<Integer> ids=get(field, dto);
                List<Integer> searchIds=get(field, searchDto.getDto());
                if (searchIds != null && ids!=null) {
                    if (searchDto.getMode().equals(ExampleMatcher.MatchMode.ALL)) {
                        filtered[0] = filtered[0] && filterList(get(field, dto), get(field, searchDto.getDto()));
                    } else {
                        filtered[0] = filtered[0] || filterList(get(field, dto), get(field, searchDto.getDto()));
                    }
                }


            }
        });

        return filtered[0];
    }


    @Override
    public List<D> sort(SortDTO sortDTO) {
        try {
            String field = getPath(findField(entityClass.getDeclaredConstructor().newInstance(), sortDTO.getField()).toArray(new String[]{}));
            return mapper.convertList(repository.findAll(Sort.by(sortDTO.getDirection(), field)), dtoClass);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<D> sortSearch(SearchDTO<D> searchDto, SortDTO sortDTO) {
        return mapper.convertList(repository.findAll(
                        Example.of(mapper.convert(searchDto.getDto(), entityClass),
                                getMatcher(searchDto.getDto(), searchDto.getMode(), searchDto.getType())),
                        Sort.by(sortDTO.getDirection(), sortDTO.getField())),
                dtoClass
        );
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saves(List<D> ds) {
        List<D> list = ds.stream()
                .filter(dto -> !search(new SearchDTO<>(dto, ExampleMatcher.MatchMode.ALL, ExampleMatcher.StringMatcher.EXACT, null, null)).isEmpty()).toList();
        if (list.isEmpty()) {
            repository.saveAll(mapper.convertList(ds, entityClass));
        }
    }

    @Override
    public void delete(int id) {
        repository.deleteById(id);
    }
}

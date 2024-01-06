package fr.perso.springserie.controller;

import fr.perso.springserie.model.PagedResponse;
import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.dto.special.SearchDTO;
import fr.perso.springserie.model.dto.special.SortDTO;
import fr.perso.springserie.model.dto.special.SortSearchDTO;
import fr.perso.springserie.service.interfaces.crud.IBaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Consumer;

public abstract class BaseController<D extends BaseDTO> {
    protected final IBaseService<D> service;

    protected BaseController(IBaseService<D> service) {
        this.service = service;
    }

    protected void formatDTO(List<D> dtos) {
        dtos.forEach(getConsumer());
    }

    protected Consumer<D> getConsumer() {
        return d -> {
        };
    }

    @GetMapping("/list")
    public ResponseEntity<List<D>> getAll() {
        List<D> all = service.getAll();
        formatDTO(all);
        return ResponseEntity.ok(all);
    }

    @GetMapping("/paged/list")
    public ResponseEntity<PagedResponse<D>> getAll(int size, int page) {
        return ResponseEntity.ok(service.getAll(size, page));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<D> getById(@PathVariable int id) {
        return ResponseEntity.ofNullable(service.getById(id));
    }

    @PostMapping("/byIds")
    public ResponseEntity<List<D>> getByIds(@RequestBody List<Integer> ids) {
        List<D> list = service.getByIds(ids);
        formatDTO(list);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/search")
    public ResponseEntity<List<D>> search(@RequestBody SearchDTO<D> searchDTO) {
        List<D> search = service.search(searchDTO);
        formatDTO(search);
        return ResponseEntity.ok(search);
    }

    @PostMapping("/paged/search")
    public ResponseEntity<PagedResponse<D>> search(@RequestBody SearchDTO<D> searchDTO, int size, int page) {
        PagedResponse<D> response = service.search(searchDTO, size, page);
        formatDTO(response.getContent());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sort")
    public ResponseEntity<List<D>> sort(@RequestBody SortDTO sortDTO) {
        List<D> sort = service.sort(sortDTO);
        formatDTO(sort);
        return ResponseEntity.ok(sort);
    }

    @PostMapping("/paged/sort")
    public ResponseEntity<PagedResponse<D>> sort(@RequestBody SortDTO sortDTO, int size, int page) {
        PagedResponse<D> response = service.sort(sortDTO, size, page);
        formatDTO(response.getContent());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sort/search")
    public ResponseEntity<List<D>> sortSearch(@RequestBody SortSearchDTO<D> sortSearchDTO) {
        List<D> sortSearch = service.sortSearch(sortSearchDTO.getSearchDTO(), sortSearchDTO.getSortDTO());
        formatDTO(sortSearch);
        return ResponseEntity.ok(sortSearch);
    }

    @PostMapping("/paged/sort/search")
    public ResponseEntity<PagedResponse<D>> sortSearch(@RequestBody SortSearchDTO<D> sortSearchDTO, int size, int page) {
        PagedResponse<D> response = service.sortSearch(sortSearchDTO.getSearchDTO(), sortSearchDTO.getSortDTO(), size, page);
        formatDTO(response.getContent());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<D> save(@RequestBody D dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @PostMapping("/saves")
    public void saves(@RequestBody List<D> ds) {
        service.saves(ds);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable int id) {
        service.delete(id);
    }
}

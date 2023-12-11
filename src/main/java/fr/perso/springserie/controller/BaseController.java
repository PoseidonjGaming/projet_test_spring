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

public abstract class BaseController<D extends BaseDTO> {
    protected final IBaseService<D> service;

    protected BaseController(IBaseService<D> service) {
        this.service = service;
    }

    @GetMapping("/list")
    public ResponseEntity<List<D>> getAll() {
        return ResponseEntity.ok(service.getAll());
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
    public ResponseEntity<List<D>> getByIds(@RequestBody List<Integer> ids){
        return ResponseEntity.ok(service.getByIds(ids));
    }

    @PostMapping("/search")
    public ResponseEntity<List<D>> search(@RequestBody SearchDTO<D> searchDTO) {
        return ResponseEntity.ok(service.search(searchDTO));
    }

    @PostMapping("/paged/search")
    public ResponseEntity<PagedResponse<D>> search(@RequestBody SearchDTO<D> searchDTO, int size, int page) {
        return ResponseEntity.ok(service.search(searchDTO, size, page));
    }

    @PostMapping("/sort")
    public ResponseEntity<List<D>> sort(@RequestBody SortDTO sortDTO) {
        return ResponseEntity.ok(service.sort(sortDTO));
    }

    @PostMapping("/paged/sort")
    public ResponseEntity<PagedResponse<D>> sort(@RequestBody SortDTO sortDTO, int size, int page) {
        return ResponseEntity.ok(service.sort(sortDTO, size, page));
    }

    @PostMapping("/sort/search")
    public ResponseEntity<List<D>> sortSearch(@RequestBody SortSearchDTO<D> sortSearchDTO) {
        return ResponseEntity.ok(service.sortSearch(sortSearchDTO.getSearchDTO(), sortSearchDTO.getSortDTO()));
    }

    @PostMapping("/paged/sort/search")
    public ResponseEntity<PagedResponse<D>> sortSearch(@RequestBody SortSearchDTO<D> sortSearchDTO, int size, int page) {
        return ResponseEntity.ok(service.sortSearch(sortSearchDTO.getSearchDTO(), sortSearchDTO.getSortDTO(), size, page));
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

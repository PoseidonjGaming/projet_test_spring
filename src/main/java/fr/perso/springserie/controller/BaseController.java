package fr.perso.springserie.controller;

import fr.perso.springserie.model.PageRequest;
import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.dto.special.SearchDateDto;
import fr.perso.springserie.model.dto.special.SearchDto;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.service.interfaces.IBaseService;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

public abstract class BaseController<D extends BaseDTO> {

    protected final IBaseService<D> service;

    protected BaseController(IBaseService<D> service) {
        this.service = service;
    }

    @GetMapping("/list")
    public ResponseEntity<PageRequest<D>> getAll(int size, int page) {
        return ResponseEntity.ok(service.getAll(size, page));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<D> getById(@PathVariable int id) {
        return ResponseEntity.ofNullable(service.getById(id));
    }

    @PostMapping("/byIds")
    public ResponseEntity<List<D>> getByIds(@RequestBody List<Integer> ids) {
        return ResponseEntity.ok(service.getBydIds(ids));
    }

    @PostMapping("/search")
    public ResponseEntity<List<D>> search(@RequestBody D dto, SearchDto searchDto, SearchDateDto searchDateDto) {
        return ResponseEntity.ok(service.search(dto, searchDto, searchDateDto));
    }

    @GetMapping("/sort")
    public ResponseEntity<List<D>> sort(String field, Sort.Direction direction) {
        return ResponseEntity.ok(service.sort(field, direction));
    }

    @GetMapping("/sort/search")
    public ResponseEntity<List<D>> sortSearch(@RequestBody D dto, ExampleMatcher.MatchMode mode, ExampleMatcher.StringMatcher type, String field, Sort.Direction direction) {
        return ResponseEntity.ok(service.sortSearch(field, direction, dto, mode, type));
    }

    @PostMapping("/save")
    public void save(@RequestBody D d) {
        service.save(d);
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

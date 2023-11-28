package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.service.interfaces.IBaseService;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class BaseController<E extends BaseEntity, D extends BaseDTO> {

    protected final IBaseService<D> service;

    protected BaseController(IBaseService<D> service) {
        this.service = service;
    }

    @GetMapping("/list")
    public List<D> getAll() {
        return service.getAll();
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<D> getById(@PathVariable int id) {
        return ResponseEntity.ofNullable(service.getById(id));
    }

    @PostMapping("/byIds")
    public ResponseEntity<List<D>> getByIds(@RequestBody List<Integer> ids) {
        return ResponseEntity.ok(service.getBydIds(ids));
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

    @PostMapping("/search")
    public ResponseEntity<List<D>> search(@RequestBody D dto, ExampleMatcher.MatchMode mode, ExampleMatcher.StringMatcher type) {
        return ResponseEntity.ok(service.search(dto, mode, type));
    }

    @GetMapping("/sort")
    public ResponseEntity<List<D>> sort(String field, Sort.Direction direction){
        return ResponseEntity.ok(service.sort(field, direction));
    }

    @GetMapping("/sort/search")
    public ResponseEntity<List<D>> sortSearch(@RequestBody D dto, ExampleMatcher.MatchMode mode, ExampleMatcher.StringMatcher type, String field, Sort.Direction direction){
        return ResponseEntity.ok(service.sortSearch(field, direction, dto, mode, type));
    }
}

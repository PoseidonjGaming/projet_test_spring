package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.BaseDTO;
import fr.perso.springserie.model.entity.BaseEntity;
import fr.perso.springserie.service.interfaces.IBaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class BaseController<E extends BaseEntity, D extends BaseDTO> {

    protected final IBaseService<E, D> service;

    public BaseController(IBaseService<E, D> service) {
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

    @GetMapping("/search")
    public abstract ResponseEntity<List<D>> search(String term);

    @PostMapping("/save")
    public void save(@RequestBody D d) {
        service.save(d);
    }

    @PostMapping("/saves")
    public void saves(@RequestBody List<D> ds) {
        ds.forEach(service::save);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable int id) {
        service.delete(id);
    }

}

package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.service.interfaces.crud.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/watchlist")
public class WatchListController {

    private final IUserService service;

    public WatchListController(IUserService service) {
        this.service = service;
    }

    @GetMapping("/add/{type}/{id}")
    public ResponseEntity<List<Integer>> addToWatchList(@PathVariable String type, @PathVariable int id, String username) {
        return ResponseEntity.ok(service.addToWatchList(type, id, username));
    }

    @GetMapping("/remove/{type}/{id}")
    public ResponseEntity<List<Integer>> removeWatch(@PathVariable String type, @PathVariable int id, String username) {
        return ResponseEntity.ok(service.removeFromWatchList(type, id, username));
    }
}

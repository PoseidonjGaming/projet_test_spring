package fr.perso.springserie.controller.crudl;

import fr.perso.springserie.model.dto.EpisodeDTO;
import fr.perso.springserie.model.entity.Episode;
import fr.perso.springserie.service.interfaces.crud.IEpisodeService;
import jakarta.persistence.Entity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@RestController
@RequestMapping("/episode")
public class EpisodeController extends BaseController<EpisodeDTO, IEpisodeService> {

    protected EpisodeController(IEpisodeService service) {
        super(service);
    }

    @GetMapping("/bySeasons/{id}")
    public ResponseEntity<List<EpisodeDTO>> getBySeason(@PathVariable("id") List<Integer> id) {
        return ResponseEntity.ofNullable(service.getBySeasonIdIn(id));
    }



}

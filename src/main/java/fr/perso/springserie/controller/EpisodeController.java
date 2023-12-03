package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.EpisodeDTO;
import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.model.entity.Episode;
import fr.perso.springserie.service.interfaces.IEpisodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/episode")
public class EpisodeController extends BaseController<EpisodeDTO> {

    @Autowired
    public EpisodeController(IEpisodeService service) {
        super(service);
    }

    @GetMapping("/bySeasons/{id}")
    public ResponseEntity<List<EpisodeDTO>> getBySeason(@PathVariable("id") List<Integer> id) {
        return ResponseEntity.ofNullable(((IEpisodeService) service).getBySeasonIdIn(id));
    }

}

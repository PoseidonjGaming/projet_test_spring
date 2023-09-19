package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.SeasonDTO;
import fr.perso.springserie.model.entity.Season;
import fr.perso.springserie.service.interfaces.ISeasonService;
import fr.perso.springserie.service.interfaces.ISeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/season")
public class SeasonController extends BaseController<Season, SeasonDTO> {

    private final ISeriesService seriesService;

    @Autowired
    public SeasonController(ISeasonService service, ISeriesService seriesService) {
        super(service);
        this.seriesService = seriesService;
    }

    @GetMapping("/bySeries/{id}")
    public ResponseEntity<List<SeasonDTO>> getBySeriesId(@PathVariable int id) {
        return ResponseEntity.ofNullable(((ISeasonService) service).getBySeriesId(id));
    }

}

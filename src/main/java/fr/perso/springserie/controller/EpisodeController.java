package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.EpisodeDTO;
import fr.perso.springserie.service.interfaces.crud.IBaseService;
import fr.perso.springserie.service.interfaces.crud.IEpisodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/episode")
public class EpisodeController extends BaseController<EpisodeDTO, IEpisodeService>{
    protected EpisodeController(IEpisodeService service) {
        super(service);
    }


    @GetMapping("/bySeasons/{id}")
    public ResponseEntity<List<EpisodeDTO>> getBySeason(@PathVariable("id") List<Integer> id) {
        return ResponseEntity.ofNullable(((IEpisodeService) service).getBySeasonIdIn(id));
    }
}

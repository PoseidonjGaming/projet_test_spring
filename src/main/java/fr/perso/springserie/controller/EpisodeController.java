package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.EpisodeDTO;
import fr.perso.springserie.model.entity.Episode;
import fr.perso.springserie.service.interfaces.IEpisodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/episode")
public class EpisodeController extends BaseController<Episode, EpisodeDTO>{

    @Autowired
    public EpisodeController(IEpisodeService service) {
        super(service);
    }

    @GetMapping("/bySaisons/{id}")
    public ResponseEntity<List<EpisodeDTO>> getBySaison(@PathVariable List<Integer> id){
        return ResponseEntity.ok(((IEpisodeService)service).getBySaisonIdIn(id));
    }
}

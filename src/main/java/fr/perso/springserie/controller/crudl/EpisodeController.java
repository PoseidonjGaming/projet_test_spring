package fr.perso.springserie.controller.crudl;

import fr.perso.springserie.model.dto.EpisodeDTO;
import fr.perso.springserie.model.entity.Episode;
import fr.perso.springserie.repository.IEpisodeRepo;
import fr.perso.springserie.service.interfaces.crud.IEpisodeService;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/episode")
public class EpisodeController extends BaseController<EpisodeDTO, IEpisodeService> {

    private final IEpisodeRepo repo;

    protected EpisodeController(IEpisodeService service, IEpisodeRepo repo) {
        super(service);
        this.repo = repo;
    }

    @GetMapping("/bySeasons/{id}")
    public ResponseEntity<List<EpisodeDTO>> getBySeason(@PathVariable("id") List<Integer> id) {
        return ResponseEntity.ofNullable(service.getBySeasonIdIn(id));
    }

    @GetMapping("/test")
    public ResponseEntity<List<Episode>> test() {
        return ResponseEntity.ok(repo.findAll());
    }


}

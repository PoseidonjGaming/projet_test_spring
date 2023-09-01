package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.SaisonDTO;
import fr.perso.springserie.model.entity.Saison;
import fr.perso.springserie.service.interfaces.ISaisonService;
import fr.perso.springserie.service.interfaces.ISeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/saison")
public class SaisonController extends BaseController<Saison, SaisonDTO> {

    private final ISeriesService seriesService;

    @Autowired
    public SaisonController(ISaisonService service, ISeriesService seriesService) {
        super(service);
        this.seriesService = seriesService;
    }

    @GetMapping("/bySeries/{id}")
    public ResponseEntity<List<SaisonDTO>> getBySeriesId(@PathVariable int id) {
        return ResponseEntity.ofNullable(((ISaisonService)service).getBySeriesId(id));
    }

}

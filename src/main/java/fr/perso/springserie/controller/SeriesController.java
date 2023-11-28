package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.model.entity.Series;
import fr.perso.springserie.service.interfaces.IFileService;
import fr.perso.springserie.service.interfaces.ISeriesService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SeriesController extends BaseController<Series, SeriesDTO> {

    private final IFileService fileService;


    public SeriesController(ISeriesService service, IFileService fileService) {
        super(service);
        this.fileService = fileService;
    }

    @PostMapping(value = "/save", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> save(@RequestPart("file") MultipartFile file, @RequestPart("series") String series) {
        ((ISeriesService) service).saveWithFile(file, series);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/byCategories")
    public ResponseEntity<List<SeriesDTO>> byCategories(@RequestBody List<Integer> ids) {
        return ResponseEntity.ok(((ISeriesService) service).getByCategoryIds(ids));
    }

    @PostMapping("/save/seasons")
    public ResponseEntity<SeriesDTO> saveWithSeasons(@RequestBody SeriesDTO dto, int seasons){
        return ResponseEntity.ok(((ISeriesService)service).savesWithSeasons(dto, seasons));
    }
}

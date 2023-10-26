package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.model.dto.special.SearchSeries;
import fr.perso.springserie.model.entity.Series;
import fr.perso.springserie.service.interfaces.IFileService;
import fr.perso.springserie.service.interfaces.ISeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SeriesController extends BaseController<Series, SeriesDTO> {

    private final IFileService fileService;

    @Autowired
    public SeriesController(ISeriesService service, IFileService fileService) {
        super(service);
        this.fileService = fileService;
    }

    @PostMapping(value = "/save", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> save(@RequestPart("file") MultipartFile file, @RequestPart("series") String series) {
        ((ISeriesService) service).saveWithFile(file, series);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/save/seasons")
    public void saveWithSeasons(SeriesDTO series) {
        ((ISeriesService) service).saveWithSeasons(series);
    }






    @PostMapping("/byCategories")
    public ResponseEntity<List<SeriesDTO>> byCategories(@RequestBody List<Integer> ids) {
        return ResponseEntity.ok(((ISeriesService) service).getByCategoryIds(ids));
    }

    @PostMapping("/filteredSearch")
    public ResponseEntity<List<SeriesDTO>> filteredSearch(@RequestBody SearchSeries search) {
        return ResponseEntity.ok(((ISeriesService) service).search(search.getTerm(), search.getIds()));
    }
}

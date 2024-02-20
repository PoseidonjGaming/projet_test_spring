package fr.perso.springserie.controller.crudl;

import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.service.interfaces.crud.ISeriesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/series")
public class SeriesController extends BaseController<SeriesDTO, ISeriesService> {
    protected SeriesController(ISeriesService service) {
        super(service);
    }

    @PostMapping(value = "/save", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<HttpStatus> save(@RequestPart("file") MultipartFile file, @RequestPart("series") String series) {
        service.saveWithFile(file, series);
        return ResponseEntity.ok().build();
    }
}

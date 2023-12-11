package fr.perso.springserie.controller;

import fr.perso.springserie.model.dto.SeriesDTO;
import fr.perso.springserie.service.interfaces.crud.IBaseService;
import fr.perso.springserie.service.interfaces.crud.ISeriesService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@RestController
@RequestMapping("/series")
public class SeriesController  extends BaseController<SeriesDTO>{
    protected SeriesController(ISeriesService service) {
        super(service);
    }

    @PostMapping(value = "/save", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> save(@RequestPart("file") MultipartFile file, @RequestPart("series") String series) {
        ((ISeriesService) service).saveWithFile(file, series);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/save/seasons")
    public ResponseEntity<SeriesDTO> saveWithSeasons(@RequestBody SeriesDTO dto, int seasons){
        return ResponseEntity.ok(((ISeriesService)service).savesWithSeasons(dto, seasons));
    }
}

package fr.perso.springserie.controller;

import fr.perso.springserie.service.interfaces.IFileService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    private final IFileService service;

    public FileController(IFileService service) {
        this.service = service;
    }

    @PostMapping("/export")
    public ResponseEntity<InputStreamResource> test(@RequestBody List<Boolean> booleanList) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(new InputStreamResource(service.writeExcel(booleanList)));
    }
}

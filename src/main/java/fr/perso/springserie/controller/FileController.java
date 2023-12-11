package fr.perso.springserie.controller;

import fr.perso.springserie.service.interfaces.IFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    private final IFileService service;

    public FileController(IFileService service) {
        this.service = service;
    }

    @PostMapping("/saves")
    public ResponseEntity<?> saveFiles(@RequestBody List<MultipartFile> files, String type) {
        service.saves(files, type);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveFile(@RequestBody MultipartFile file, String type) {
        service.save(file, type);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/load")
    public ResponseEntity<?> load(String filename) {
        return service.load(filename);
    }

    @PostMapping("/export")
    public ResponseEntity<?> test(@RequestBody List<Boolean> booleanList) {
        return service.writeExcel(booleanList);
    }


}

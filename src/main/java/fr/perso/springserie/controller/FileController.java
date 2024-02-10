package fr.perso.springserie.controller;

import fr.perso.springserie.service.interfaces.IFileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    private final IFileService service;

    public FileController(IFileService service) {
        this.service = service;
    }

    @PostMapping("/saves")
    public ResponseEntity<HttpStatus> saveFiles(@RequestBody List<MultipartFile> files, String type) {
        service.saves(files, type);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/save")
    public ResponseEntity<HttpStatus> saveFile(@RequestBody MultipartFile file, String type) {
        service.save(file, type);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/load")
    public ResponseEntity<byte[]> load(String filename) {
        return service.load(filename);
    }

    @PostMapping(value = "/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> exportXlsx(@RequestBody List<String> classList) {
        return service.writeExcel(classList);
    }

    @PostMapping("/import")
    public ResponseEntity<HttpStatus> importXlsx(@RequestBody MultipartFile file) {
        service.readExcel(file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test")
    public ResponseEntity<LocalDate> test() {
        return ResponseEntity.ok(LocalDate.ofEpochDay(44211));
    }


}

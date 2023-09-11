package fr.perso.springserie.controller;

import fr.perso.springserie.service.interfaces.IFileService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.InputStream;

@RestController
@RequestMapping("/file")
public class FileController {

    private final IFileService service;

    public FileController(IFileService service) {
        this.service = service;
    }

    @GetMapping("/test")
    public ResponseEntity<InputStreamResource> test(){
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(new InputStreamResource(service.writeExcel()));
    }
}

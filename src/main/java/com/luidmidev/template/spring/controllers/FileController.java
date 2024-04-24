package com.luidmidev.template.spring.controllers;


import com.luidmidev.template.spring.exceptions.ClientException;
import com.luidmidev.template.spring.utils.StringUtils;
import com.waipersoft.store.FileStoreService;
import com.waipersoft.store.FileStoreService.FileInfo;
import com.waipersoft.store.targets.mongo.GridFSFileStoreService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.waipersoft.store.FileStoreUtils.getHeaders;


@RestController
@RequestMapping("/files")
public class FileController {

    private final FileStoreService service;

    FileController(GridFSFileStoreService service) {
        this.service = service;
    }

    /**
     * Descarga el archivo correspondiente al ID especificado.
     *
     * @param id ID del archivo a descargar.
     * @return Una ResponseEntity que contiene el archivo como recurso de matriz de bytes si se descarga correctamente, o un mensaje de error si ocurre algún problema.
     * @throws IOException Si ocurre un error durante la descarga del archivo.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ByteArrayResource> download(@PathVariable String id, @RequestParam(required = false) String inline) throws IOException {

        var loadFile = service.download(id);

        if (loadFile == null) {
            throw new ClientException("El recurso ya no esta disponible", HttpStatus.NOT_FOUND);
        }

        var isInline = inline != null && inline.equals("true");

        var fileInfo = loadFile.getInfo();
        var filename = StringUtils.normalice(fileInfo.getFilename());

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(fileInfo.getFileType()))
                .headers(getHeaders(filename, isInline))
                .body(new ByteArrayResource(loadFile.getFile()));
    }


    @PostMapping("/upload")
    public ResponseEntity<String> upload(MultipartFile file) throws IOException {
        if (file == null) {
            throw new ClientException("No se ha enviado ningún archivo");
        }
        return ResponseEntity.ok(service.store(file));
    }

    /**
     * Elimina un archivo específico.
     *
     * @param id el identificador del archivo a eliminar
     * @return ResponseEntity con el estado de la operación y un mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        service.remove(id);
        return ResponseEntity.ok("Eliminado");
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<FileInfo> info(@PathVariable String id) throws IOException {
        var loadFile = service.info(id);
        return ResponseEntity.ok(loadFile);
    }


}
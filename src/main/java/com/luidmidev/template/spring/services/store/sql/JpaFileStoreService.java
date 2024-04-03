package com.luidmidev.template.spring.services.store.sql;

import com.luidmidev.template.spring.exceptions.ClientException;
import com.luidmidev.template.spring.services.store.FileStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


@Service
@Transactional
public class JpaFileStoreService implements FileStoreService {
    private final FileStoreRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(JpaFileStoreService.class);

    @Autowired
    JpaFileStoreService(FileStoreRepository repository) {
        this.repository = repository;
        repository.deleteAllByEphimeralTrue();
        logger.info("Se eliminaron los archivos efímeros de la base de datos");
    }

    @Override
    public String store(MultipartFile upload) throws IOException {
        return internalAddFile(upload.getInputStream(), upload.getOriginalFilename(), false);
    }

    @Override
    public String store(InputStream upload, String filename) throws IOException {
        return internalAddFile(upload, filename, false);
    }

    @Override
    public String storeEphimeral(InputStream upload, String filename) throws IOException {
        return internalAddFile(upload, filename, true);
    }

    private String internalAddFile(InputStream upload, String filename, boolean ephimeral) throws IOException {

        if (filename == null || filename.isEmpty()) {
            throw new ClientException("El nombre del archivo no puede estar vacío");
        }

        filename = filename.replaceAll("\"", "");

        var contentType = guessContentType(filename);
        var fileBytes = upload.readAllBytes();

        var dbFile = FileStore.builder()
                .content(fileBytes)
                .contentType(contentType)
                .contentLength((long) fileBytes.length)
                .originalFileName(filename)
                .ephimeral(ephimeral)
                .build();

        var saved = repository.save(dbFile);
        return saved.getId().toString();
    }

    @Override
    public DownloadedFile download(String id) {

        var uuid = UUID.fromString(id);

        var dbFile = repository.findById(uuid).orElseThrow(this::notFoundFileException);

        var loadFile = DownloadedFile.builder()
                .filename(dbFile.getOriginalFileName())
                .fileType(dbFile.getContentType())
                .fileSize(dbFile.getContentLength())
                .file(dbFile.getContent())
                .build();

        if (dbFile.getEphimeral()) repository.delete(dbFile);

        return loadFile;
    }

    @Override
    public FileInfo info(String id) {
        var uuid = UUID.fromString(id);
        var dbFileInfo = repository.findProjectedById(uuid).orElseThrow(this::notFoundFileException);
        return FileInfo.builder()
                .filename(dbFileInfo.getOriginalFileName())
                .fileType(dbFileInfo.getContentType())
                .fileSize(dbFileInfo.getContentLength())
                .build();
    }

    @Override
    public void remove(String id) {
        try {
            var uuid = UUID.fromString(id);
            repository.deleteById(uuid);
        } catch (Exception ignored) {
            throw new ClientException("No se pudo eliminar el archivo");
        }
    }

    private ClientException notFoundFileException() {
        return new ClientException("No se pudo encontrar el archivo");
    }

}

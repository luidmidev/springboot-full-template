package com.luidmidev.template.spring.services.store.mongo;

import com.luidmidev.template.spring.services.store.FileStoreService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * Servicio para operaciones relacionadas con archivos.
 */
@Service
public class GridFSFileStoreService implements FileStoreService {

    private final static Logger logger = LoggerFactory.getLogger(GridFSFileStoreService.class);
    private final GridFsTemplate template;
    private final GridFsOperations operations;

    @Autowired
    public GridFSFileStoreService(GridFsTemplate template, GridFsOperations operations) {
        this.template = template;
        this.operations = operations;
        removeEmphimeralFiles();
    }

    private void removeEmphimeralFiles() {
        Query query = new Query(Criteria.where("metadata.ephimeral").is(true));
        template.delete(query);
        logger.info("Deleting ephimeral files in collections ps.files and ps.chunks");
    }

    public String store(MultipartFile upload) throws IOException {
        return internalAddFile(upload.getInputStream(), upload.getOriginalFilename(), false);
    }


    public String store(InputStream upload, String filename) throws IOException {
        return internalAddFile(upload, filename, false);
    }

    public String storeEphimeral(InputStream upload, String filename) throws IOException {
        return internalAddFile(upload, filename, true);
    }

    private String internalAddFile(InputStream upload, String filename, Boolean ephimeral) throws IOException {

        var contentType = guessContentType(filename);
        var fileBytes = upload.readAllBytes();

        DBObject metadata = new BasicDBObject();
        metadata.put("fileSize", fileBytes.length);
        metadata.put("dateUpload", LocalDateTime.now());
        if (ephimeral) metadata.put("ephimeral", true);
        ObjectId fileID = template.store(new ByteArrayInputStream(fileBytes), filename, contentType, metadata);
        return fileID.toString();
    }


    public DownloadedFile download(String id) throws IOException {

        var gridFSFile = template.findOne(new Query(Criteria.where("_id").is(id)));
        var loadFileBuilder = DownloadedFile.builder();

        if (gridFSFile == null) return null;

        if (gridFSFile.getMetadata() != null) {

            var metadata = gridFSFile.getMetadata();

            var fileInfo = FileInfo.builder()
                    .filename(gridFSFile.getFilename())
                    .fileType(metadata.get("_contentType").toString())
                    .fileSize(Long.parseLong(metadata.get("fileSize").toString()))
                    .build();

            loadFileBuilder.file(IOUtils.toByteArray(operations.getResource(gridFSFile).getInputStream()));
            loadFileBuilder.info(fileInfo);

            if (metadata.containsKey("ephimeral") && metadata.getBoolean("ephimeral")) {
                remove(id);
            }
        }

        return loadFileBuilder.build();
    }

    @Override
    public FileInfo info(String id) {

        var gridFSFile = template.findOne(new Query(Criteria.where("_id").is(id)));
        var builder = FileInfo.builder();

        if (gridFSFile == null) return null;

        if (gridFSFile.getMetadata() != null) {

            var metadata = gridFSFile.getMetadata();

            builder
                    .filename(gridFSFile.getFilename())
                    .fileType(metadata.get("_contentType").toString())
                    .fileSize(Long.parseLong(metadata.get("fileSize").toString()));

        }

        return builder.build();

    }

    public void remove(String id) {
        template.delete(new Query(Criteria.where("_id").is(id)));
    }

}
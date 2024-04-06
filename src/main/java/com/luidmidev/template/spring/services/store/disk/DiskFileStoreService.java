package com.luidmidev.template.spring.services.store.disk;

import com.luidmidev.template.spring.services.store.FileStoreService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class DiskFileStoreService implements FileStoreService {
    private final String storagePath;

    public DiskFileStoreService(@Value("${filestorage.path}") String storagePath) {
        if (storagePath.contains("{user.dir}")) {
            this.storagePath = storagePath.replace("{user.dir}", System.getProperty("user.dir"));
        } else {
            this.storagePath = storagePath;
        }
        createPathIfNotExists(this.storagePath);
        createPathIfNotExists(this.storagePath + "/ephimeral");
    }

    @Override
    public String store(MultipartFile upload) throws IOException {
        var filename = upload.getOriginalFilename();
        if (filename == null || filename.isEmpty()) throw new IllegalArgumentException("Filename cannot be empty");
        var path = storagePath + "/" + UUID.randomUUID() + getExtension(filename);
        upload.transferTo(new File(path));
        return path;
    }

    @Override
    public String store(InputStream upload, String filename) throws IOException {
        var path = storagePath + "/" + UUID.randomUUID() + getExtension(filename);
        return internalStore(upload, path);

    }

    public String storeInFolder(InputStream upload, String filename, String folder) throws IOException {
        if (folder.charAt(0) == '/') folder = folder.substring(1);
        var p = storagePath + "/" + folder + "/" + UUID.randomUUID() + getExtension(filename);
        return internalStore(upload, p);

    }

    @Override
    public String storeEphimeral(InputStream upload, String filename) throws IOException {
        var path = storagePath + "/ephimeral/" + UUID.randomUUID() + getExtension(filename);
        return internalStore(upload, path);
    }

    @Override
    public DownloadedFile download(String id) throws IOException {
        var file = loadFile(id);
        var bytes = new byte[(int) file.length()];
        var fis = new FileInputStream(file);
        var read = fis.read(bytes);
        if (read != file.length())
            throw new IOException("File not read correctly: " + read + " of " + file.length() + " bytes on " + file.getName());
        fis.close();

        var filename = file.getName();

        return DownloadedFile.builder()
                .file(bytes)
                .info(FileInfo.builder()
                        .filename(filename)
                        .fileType(guessContentType(filename))
                        .fileSize(file.length())
                        .build()
                ).build();

    }

    @Override
    public FileInfo info(String id) throws IOException {
        Path path = loadPath(id);
        var filename = path.getFileName().toString();
        return FileInfo.builder()
                .filename(filename)
                .fileType(guessContentType(filename))
                .fileSize(Files.size(path))
                .build();
    }

    private File loadFile(String name) {
        var file = new File(storagePath + "/" + name);
        if (file.exists()) return file;

        var ephimeral = new File(storagePath + "/ephimeral/" + name);
        if (ephimeral.exists()) return ephimeral;

        throw new IllegalArgumentException("File not found " + name);
    }

    private Path loadPath(String name) {
        var path = Paths.get(storagePath + "/" + name);
        if (Files.exists(path)) return path;
        var ephimeral = Paths.get(storagePath + "/ephimeral/" + name);
        if (Files.exists(ephimeral)) return ephimeral;
        throw new IllegalArgumentException("Path not found " + name);
    }

    private static void createPathIfNotExists(String path) {
        var dirs = new File(path);
        if (!dirs.exists()) {
            var created = dirs.mkdirs();
            if (!created) throw new IllegalArgumentException("Path not created: " + path);
        }
    }

    private static String internalStore(InputStream upload, String path) throws IOException {
        var file = new File(path);
        var created = file.createNewFile();
        if (!created) throw new IOException("File not created");
        var bytes = upload.readAllBytes();
        var fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.close();
        return path;
    }

    private static String getExtension(String filename) {
        var parts = filename.split("\\.");
        return parts[parts.length - 1];
    }

    @Override
    public void remove(String id) {
        var file = loadFile(id);
        if (!file.delete()) throw new IllegalArgumentException("File not deleted");
    }
}

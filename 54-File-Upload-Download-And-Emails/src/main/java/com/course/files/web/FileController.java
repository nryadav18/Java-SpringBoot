package com.course.files.web;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.course.files.storage.FileStorageService;
import com.course.files.storage.StoredFile;

/*
 * Upload and download endpoints.
 *
 * UPLOAD uses "multipart/form-data" - the same format a browser uses for <form enctype="multipart/form-data">.
 * The request is split into "parts"; each file part becomes a MultipartFile in Java.
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    /** Only these types may be shown INLINE in the browser. Everything else is always downloaded. */
    private static final Set<MediaType> INLINE_TYPES = Set.of(
            MediaType.IMAGE_PNG, MediaType.IMAGE_JPEG, MediaType.IMAGE_GIF,
            MediaType.parseMediaType("image/webp"), MediaType.APPLICATION_PDF);

    private final FileStorageService storageService;

    public FileController(FileStorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * SINGLE upload.  curl -F "file=@photo.png" http://localhost:8080/api/files
     * "file" = the name of the form field (part).
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StoredFile> upload(@RequestParam("file") MultipartFile file) {
        StoredFile stored = storageService.store(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(stored);
    }

    /**
     * MULTIPLE upload: several parts with the SAME field name "files".
     * curl -F "files=@a.txt" -F "files=@b.png" http://localhost:8080/api/files/batch
     */
    @PostMapping(path = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<StoredFile>> uploadMany(@RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity.status(HttpStatus.CREATED).body(storageService.storeAll(files));
    }

    @GetMapping
    public List<StoredFile> list() {
        return storageService.listAll();
    }

    /**
     * DOWNLOAD: "Content-Disposition: attachment" tells the browser "save this as a file".
     * We send the ORIGINAL (friendly) name; filename* with UTF-8 handles names like "résumé.pdf".
     */
    @GetMapping("/{filename}")
    public ResponseEntity<Resource> download(@PathVariable String filename) {
        Resource resource = storageService.loadAsResource(filename);
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(FileStorageService.originalNameOf(filename), StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .contentType(storageService.mediaTypeOf(filename))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(resource);   // Spring streams the file - it is NOT loaded fully into memory
    }

    /**
     * VIEW (serve images / PDFs inline, e.g. <img src="/api/files/abc_photo.png/view">).
     * "Content-Disposition: inline" = "show it in the browser".
     * Only for a small whitelist of safe types; X-Content-Type-Options: nosniff stops the browser
     * from "guessing" that a file is HTML and running scripts in it.
     */
    @GetMapping("/{filename}/view")
    public ResponseEntity<Resource> view(@PathVariable String filename) {
        MediaType type = storageService.mediaTypeOf(filename);
        if (!INLINE_TYPES.contains(type)) {
            return download(filename);   // not an image/PDF -> just download it
        }
        Resource resource = storageService.loadAsResource(filename);
        return ResponseEntity.ok()
                .contentType(type)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(FileStorageService.originalNameOf(filename), StandardCharsets.UTF_8).build().toString())
                .header("X-Content-Type-Options", "nosniff")
                .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")   // browsers may cache images for 1 hour
                .body(resource);
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<Void> delete(@PathVariable String filename) {
        storageService.delete(filename);
        return ResponseEntity.noContent().build();
    }
}

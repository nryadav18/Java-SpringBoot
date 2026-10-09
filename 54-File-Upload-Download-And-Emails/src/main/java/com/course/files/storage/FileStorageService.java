package com.course.files.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.course.files.config.StorageProperties;

/*
 * Stores uploaded files on the local disk - SAFELY.
 *
 * The golden rules (see lesson section "Storing files safely"):
 *   1. NEVER trust the filename sent by the client. It can be "../../Windows/win.ini" (path traversal!)
 *   2. Sanitize it: keep only the last part, allow only simple characters.
 *   3. Make it unique: add a random prefix so two "photo.jpg" uploads do not overwrite each other.
 *   4. Whitelist file extensions.
 *   5. After building the target path, CHECK that it is still inside the upload folder.
 *   6. Store files OUTSIDE src/ and outside the static web folder, and never execute them.
 */
@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final Path rootLocation;
    private final Set<String> allowedExtensions;

    public FileStorageService(StorageProperties properties) {
        // toAbsolutePath().normalize() turns "uploads" into e.g. D:/Java-SpringBoot/54-.../uploads
        // (we write "/" here on purpose: a backslash followed by "u" inside Java source - even in a
        //  comment - is read as a unicode escape and breaks compilation! See lesson, "Common mistakes")
        this.rootLocation = Paths.get(properties.location()).toAbsolutePath().normalize();
        this.allowedExtensions = properties.allowedExtensions();
        try {
            Files.createDirectories(rootLocation);   // does nothing if the folder already exists
        } catch (IOException e) {
            throw new StorageException("Could not create upload folder " + rootLocation, e);
        }
        log.info("Uploads are stored in {}", rootLocation);
    }

    /** Saves one uploaded file and returns its information. */
    public StoredFile store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("Please choose a non-empty file");
        }

        String originalName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String safeName = sanitizeFilename(originalName);

        String extension = StringUtils.getFilenameExtension(safeName);
        if (extension == null || !allowedExtensions.contains(extension.toLowerCase(Locale.ROOT))) {
            throw new StorageException("File type not allowed: '" + originalName
                    + "'. Allowed: " + allowedExtensions);
        }

        // unique name on disk: 12 random hex characters + "_" + the cleaned name
        String storedName = UUID.randomUUID().toString().replace("-", "").substring(0, 12) + "_" + safeName;
        Path target = resolveSafely(storedName);

        // try-with-resources (topic 10): the upload stream is always closed
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Failed to store " + originalName, e);
        }

        log.info("Stored '{}' as '{}' ({} bytes, client said content type {})",
                originalName, storedName, file.getSize(), file.getContentType());
        return toStoredFile(target);
    }

    /** Saves several files - all or nothing is NOT guaranteed here; each file is validated on its own. */
    public List<StoredFile> storeAll(List<MultipartFile> files) {
        return files.stream().map(this::store).toList();
    }

    /** Lists all stored files. */
    public List<StoredFile> listAll() {
        try (Stream<Path> paths = Files.list(rootLocation)) {
            return paths.filter(Files::isRegularFile)
                    .sorted()
                    .map(this::toStoredFile)
                    .toList();
        } catch (IOException e) {
            throw new StorageException("Failed to list files", e);
        }
    }

    /** Loads a stored file as a Spring Resource (for streaming it to the client). */
    public Resource loadAsResource(String storedName) {
        Path file = resolveSafely(storedName);
        try {
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new StoredFileNotFoundException(storedName);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new StoredFileNotFoundException(storedName);
        }
    }

    /** The path on disk (used by the email service to attach a file). */
    public Path pathOf(String storedName) {
        Path file = resolveSafely(storedName);
        if (!Files.isRegularFile(file)) {
            throw new StoredFileNotFoundException(storedName);
        }
        return file;
    }

    public void delete(String storedName) {
        try {
            if (!Files.deleteIfExists(resolveSafely(storedName))) {
                throw new StoredFileNotFoundException(storedName);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to delete " + storedName, e);
        }
    }

    /** Guess the content type from the extension (we do NOT trust the type the client sent). */
    public MediaType mediaTypeOf(String filename) {
        return MediaTypeFactory.getMediaType(filename).orElse(MediaType.APPLICATION_OCTET_STREAM);
    }

    /** "3f2a9c1b7d4e_holiday_photo.png" -> "holiday_photo.png" */
    public static String originalNameOf(String storedName) {
        int underscore = storedName.indexOf('_');
        return underscore >= 0 ? storedName.substring(underscore + 1) : storedName;
    }

    /**
     * Turns ANY client filename into a harmless one:
     *   "C:\Data\me\My Photo (1).PNG"  -> "My_Photo__1_.PNG"
     *   "../../etc/passwd"             -> "passwd"
     *   "<script>.txt"                 -> "_script_.txt"
     */
    static String sanitizeFilename(String originalName) {
        // 1) cleanPath turns "\" into "/" and resolves "a/../b"; then keep only the LAST part (no folders)
        String cleaned = StringUtils.cleanPath(originalName);
        String nameOnly = StringUtils.getFilename(cleaned);
        if (nameOnly == null || nameOnly.isBlank()) {
            nameOnly = "file";
        }
        // 2) allow only letters, digits, dot, dash, underscore - everything else becomes "_"
        String safe = nameOnly.replaceAll("[^a-zA-Z0-9._-]", "_");
        // 3) no hidden files / names made only of dots, and keep the name reasonably short
        safe = safe.replaceAll("^\\.+", "");
        if (safe.isBlank()) {
            safe = "file";
        }
        return safe.length() > 100 ? safe.substring(safe.length() - 100) : safe;
    }

    /** Builds root/name and makes sure the result is STILL inside the root folder (path traversal guard). */
    private Path resolveSafely(String filename) {
        Path target = rootLocation.resolve(filename).normalize();
        if (!target.getParent().equals(rootLocation)) {
            throw new StorageException("Invalid file name (outside the upload folder): " + filename);
        }
        return target;
    }

    private StoredFile toStoredFile(Path path) {
        String storedName = path.getFileName().toString();
        long size;
        try {
            size = Files.size(path);
        } catch (IOException e) {
            size = -1;
        }
        return new StoredFile(storedName, originalNameOf(storedName), size, mediaTypeOf(storedName).toString(),
                "/api/files/" + storedName, "/api/files/" + storedName + "/view");
    }
}

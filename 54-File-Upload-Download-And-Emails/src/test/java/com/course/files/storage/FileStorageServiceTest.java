package com.course.files.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import com.course.files.config.StorageProperties;

/*
 * Plain unit tests (no Spring context) for the SECURITY rules of FileStorageService.
 * @TempDir gives every test a fresh, empty folder that JUnit deletes afterwards.
 * MockMultipartFile (from spring-test) fakes an uploaded file.
 * Run with:  mvn test
 */
class FileStorageServiceTest {

    @TempDir
    Path uploadDir;

    FileStorageService service;

    @BeforeEach
    void setUp() {
        service = new FileStorageService(new StorageProperties(uploadDir.toString(), Set.of("txt", "png", "pdf")));
    }

    @Test
    void storesAFileWithAUniqueSafeName() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "My Notes (final).txt", "text/plain",
                "hello".getBytes(StandardCharsets.UTF_8));

        StoredFile stored = service.store(file);

        assertThat(stored.storedName()).endsWith("_My_Notes__final_.txt");
        assertThat(stored.originalName()).isEqualTo("My_Notes__final_.txt");
        assertThat(Files.readString(uploadDir.resolve(stored.storedName()))).isEqualTo("hello");
    }

    @Test
    void pathTraversalInTheUploadNameIsNeutralised() {
        MockMultipartFile evil = new MockMultipartFile("file", "../../../Windows/evil.txt", "text/plain", "x".getBytes());

        StoredFile stored = service.store(evil);

        // the file landed INSIDE the upload folder, folders were stripped from the name
        assertThat(uploadDir.resolve(stored.storedName())).exists();
        assertThat(stored.storedName()).endsWith("_evil.txt").doesNotContain("..");
    }

    @Test
    void pathTraversalOnDownloadIsRejected() {
        assertThatThrownBy(() -> service.loadAsResource("../pom.xml"))
                .isInstanceOf(StorageException.class);
    }

    @Test
    void forbiddenExtensionIsRejected() {
        MockMultipartFile exe = new MockMultipartFile("file", "game.exe", "application/octet-stream", new byte[] {1, 2, 3});

        assertThatThrownBy(() -> service.store(exe))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("not allowed");
    }

    @Test
    void emptyFileIsRejected() {
        MockMultipartFile empty = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);

        assertThatThrownBy(() -> service.store(empty)).isInstanceOf(StorageException.class);
    }

    @Test
    void sanitizeExamples() {
        assertThat(FileStorageService.sanitizeFilename("C:\\Users\\me\\photo.png")).isEqualTo("photo.png");
        assertThat(FileStorageService.sanitizeFilename("<script>.txt")).isEqualTo("_script_.txt");
        assertThat(FileStorageService.sanitizeFilename(".hidden.txt")).isEqualTo("hidden.txt");
    }
}

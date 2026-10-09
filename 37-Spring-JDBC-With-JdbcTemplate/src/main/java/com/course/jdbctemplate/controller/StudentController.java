package com.course.jdbctemplate.controller;

import com.course.jdbctemplate.dto.StudentRequest;
import com.course.jdbctemplate.model.CourseStats;
import com.course.jdbctemplate.model.Student;
import com.course.jdbctemplate.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/*
 * StudentController - REST endpoints.
 *   GET    /api/students?course=Java
 *   GET    /api/students/{id}
 *   POST   /api/students            -> 201 + Location
 *   PUT    /api/students/{id}
 *   DELETE /api/students/{id}       -> 204
 *   GET    /api/students/stats      -> GROUP BY query
 *
 * (For brevity the Student record is returned directly. It has no secret fields;
 *  in a bigger app you would map to a response DTO - topic 33.)
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Student> all(@RequestParam(required = false) String course) {
        return service.findAll(course);
    }

    @GetMapping("/{id}")
    public Student one(@PathVariable long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Student> create(@Valid @RequestBody StudentRequest request) {
        Student created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable long id, @Valid @RequestBody StudentRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public List<CourseStats> stats() {
        return service.statistics();
    }
}

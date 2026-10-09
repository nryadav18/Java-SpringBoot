package com.course.jdbctemplate.service;

import com.course.jdbctemplate.dto.StudentRequest;
import com.course.jdbctemplate.exception.StudentNotFoundException;
import com.course.jdbctemplate.model.CourseStats;
import com.course.jdbctemplate.model.Student;
import com.course.jdbctemplate.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * StudentService - business layer. It depends on the StudentRepository INTERFACE,
 * so it works the same with any of the three JDBC implementations.
 */
@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public List<Student> findAll(String course) {
        return (course == null || course.isBlank()) ? repository.findAll() : repository.findByCourse(course);
    }

    public Student findById(long id) {
        return repository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    public Student create(StudentRequest request) {
        Student toSave = new Student(null, request.name(), request.email().toLowerCase(),
                request.course(), request.marks(), request.enrolledOn());
        return repository.save(toSave);   // returns the student WITH its new id
    }

    public Student update(long id, StudentRequest request) {
        Student changed = new Student(id, request.name(), request.email().toLowerCase(),
                request.course(), request.marks(), request.enrolledOn());
        if (!repository.update(changed)) {
            throw new StudentNotFoundException(id);
        }
        return changed;
    }

    public void delete(long id) {
        if (!repository.deleteById(id)) {
            throw new StudentNotFoundException(id);
        }
    }

    public List<CourseStats> statistics() {
        return repository.courseStatistics();
    }
}

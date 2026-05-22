package com.pup.taguig.app.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.pup.taguig.app.dto.StudentRequestDTO;
import com.pup.taguig.app.dto.StudentResponseDTO;
import com.pup.taguig.app.service.StudentService;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * Get all students
     * GET /api/students
     */
    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        List<StudentResponseDTO> students = studentService.retrieveAllStudent();
        return ResponseEntity.ok(students);
    }

    /**
     * Get student by ID
     * GET /api/students/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable Long id) {
        StudentResponseDTO student = studentService.getUserById(id);
        if (student != null) {
            return ResponseEntity.ok(student);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Search students by name
     * GET /api/students/search?firstName=John&lastName=Doe
     */
    @GetMapping("/search")
    public ResponseEntity<List<StudentResponseDTO>> searchByName(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        if (Objects.nonNull(firstName) && Objects.nonNull(lastName)) {
            List<StudentResponseDTO> students = studentService.searchByName(lastName, firstName);
            return ResponseEntity.ok(students);
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Create a new student
     * POST /api/students
     */
    @PostMapping
    public ResponseEntity<Long> createStudent(@RequestBody StudentRequestDTO request) {
        try {
            if (Objects.nonNull(request)) {
                Long id = studentService.insertStudent(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(id);
            }
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update an existing student
     * PUT /api/students/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable Long id,
            @RequestBody StudentRequestDTO request) {
        try {
            if (Objects.nonNull(request)) {
                StudentResponseDTO updated = studentService.updateStudent(id, request);
                if (updated != null) {
                    return ResponseEntity.ok(updated);
                }
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a student
     * DELETE /api/students/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        boolean deleted = studentService.deleteStudent(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

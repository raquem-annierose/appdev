package com.pup.taguig.app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.pup.taguig.app.model.Student;
import com.pup.taguig.app.service.StudentService;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("user")
public class UserRestController {

    private List<Student> students = null;

    @Autowired
    private StudentService studentService;

    @PostConstruct
    public void init() {
        students = new ArrayList<>();
        
        Student st1 = new Student("Annie", "Raquem", 85, 90);
        Student st2 = new Student("Rose", "Raquem", 95, 97);
        Student st3 = new Student("Yowro", "Raquem", 90, 85);
        Student st4 = new Student("Ann", "Raquem", 90, 85);
        Student st5 = new Student("Niera", "Raquem", 90, 85);
        
        students.add(st1);
        students.add(st2);
        students.add(st3);
        students.add(st4);
        students.add(st5);
    }
    
    /**
     * Get all students
     * GET /user/
     */
    @GetMapping("/")
    public List<StudentResponseDTO> getAllUsers() {
        return studentService.retrieveAllStudent();
    }
    
    /**
     * Get student by ID
     * GET /user/{id}
     */
    @GetMapping("/{id}")
    public StudentResponseDTO getUsersById(@PathVariable Long id) {
        return studentService.getUserById(id);
    }

    /**
     * Create a new student
     * POST /user/
     */
    @PostMapping("/")
    public Long addStudent(@RequestBody StudentRequestDTO student) {
        Long result = null;
        if (Objects.nonNull(student)) {
            result = studentService.insertStudent(student);
        }
        return result;
    }
    
    /**
     * Update an existing student
     * PUT /user/{id}
     */
    @PutMapping("/{id}")
    public StudentResponseDTO updateStudent(@PathVariable Long id, @RequestBody StudentRequestDTO student) {
        StudentResponseDTO result = null;
        if (Objects.nonNull(student)) {
            result = studentService.updateStudent(id, student);
        }
        return result;
    }
    
    /**
     * Search students by name
     * GET /user/filter?lastName=Doe&firstName=John
     */
    @GetMapping("/filter")
    public List<StudentResponseDTO> searchByName(
            @RequestParam("lastName") String lastName,
            @RequestParam String firstName) {
        List<StudentResponseDTO> result = new ArrayList<>();
        if (Objects.nonNull(lastName) && Objects.nonNull(firstName)) {
            result = studentService.searchByName(lastName, firstName);
        }
        return result;
    }

    /**
     * Delete a student
     * DELETE /user/{id}
     */
    @DeleteMapping("/{id}")
    public boolean deleteStudent(@PathVariable Long id) {
        return studentService.deleteStudent(id);
    }

}

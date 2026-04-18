package com.pup.taguig.app.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pup.taguig.app.dto.StudentRequestDTO;
import com.pup.taguig.app.dto.StudentResponseDTO;
import com.pup.taguig.app.model.Student;
import com.pup.taguig.app.model.User;
import com.pup.taguig.app.service.UserService;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("user")
public class UserRestController {

    private List<Student> students = null;
    
    List<User> users = new ArrayList<User>();

    @Autowired
    private UserService userService;
    
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
    
    // @GetMapping("/")
    // public List<StudentResponseDTO> getAllUsers() {
    //     return students;
    // }
    
    @GetMapping("/{id}")
    public Student getUsersById(@PathVariable Long id) {
        
        if (Objects.nonNull(students)) {
            return userService.getUserById(id);
        }
        System.out.println(students.size());
        return null;
    }

    @PostMapping("/")
    public Student addStudent(@RequestBody StudentRequestDTO student) {
        if (Objects.nonNull(student)) {
            Long id = userService.addUser(student);
            
            // Create and return the saved student with all data
            Student savedStudent = new Student(
                student.getFirstName(),
                student.getLastName(),
                student.getMidtermGrade(),
                student.getFinalGrade()
            );
            savedStudent.setId(id);
            return savedStudent;
        }
        return null;
    }


    // @PostMapping("/")
    // public Student addStudent(@RequestBody StudentRequestDTO student) {
    //     Student result = null;
    //     if (Objects.nonNull(student)) {
    //         result = userService.addUser(student);
    //     }
    //     return result;
    // }

    
    @GetMapping("/")
    public List<StudentResponseDTO> getAllPosted() {
        return userService.retrieveAllStudent();
    }

    @GetMapping("/filter")
    public List<StudentResponseDTO> searchByName(@RequestParam("lastName") String name,
            @RequestParam String firstName) {
        if (Objects.nonNull(name) && Objects.nonNull(firstName)) {
            return userService.searchByName(name, firstName);
        }
        return new ArrayList<>();
    }

    @DeleteMapping("/{id}")
    public boolean deleteStudent(@PathVariable Long id) {
        return userService.deleteStudent(id);
    }

}

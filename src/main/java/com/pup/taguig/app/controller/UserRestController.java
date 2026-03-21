package com.pup.taguig.app.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pup.taguig.app.model.Student;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("user")
public class UserRestController {

    private List<Student> students = null;
    
    @PostConstruct
    public void init() {
        students = new ArrayList<>();
        
        Student st1 = new Student(1L, "Annie", "Raquem", 85, 90);
        Student st2 = new Student(2L, "Rose", "Raquem", 95, 97);
        Student st3 = new Student(3L, "Yowro", "Raquem", 90, 85);
        Student st4 = new Student(4L, "Ann", "Raquem", 90, 85);
        Student st5 = new Student(5L, "Niera", "Raquem", 90, 85);
        
        students.add(st1);
        students.add(st2);
        students.add(st3);
        students.add(st4);
        students.add(st5);
    }
    
    @GetMapping("/")
    public List<Student> getAllUsers() {
        return students;
    }
    
    @GetMapping("/{id}")
    public Student getUsersById(@PathVariable Long id) {
        if (Objects.nonNull(students)) {
            for (Student student : students) {
                if (student.getId().equals(id)) {
                    return student;
                }
            }
        }
        System.out.println(students.size());
        return null;
    }

    @PostMapping("/")
    public Student addStudent(@RequestBody Student student) {
        if (Objects.nonNull(student)) {
            LocalDateTime date = LocalDateTime.now();
            long id = date.getDayOfYear() + 
                    date.getYear() +
                    date.getMonthValue() +
                    date.getDayOfMonth() +
                    date.getDayOfWeek().getValue() +
                    date.getHour() +
                    date.getMinute() +
                    date.getSecond() +
                    date.getNano();
            student.setId(id);
            students.add(student);
        }
        return null;
    }
    
    @GetMapping("/filterFullName")
    public List<Student> searchByFullName(@RequestParam String lastName, @RequestParam String firstName) {
        List<Student> result = new ArrayList<>();
        if (Objects.nonNull(lastName) && Objects.nonNull(firstName) && Objects.nonNull(students)) {
            for (Student student : students) {
                if (lastName.equalsIgnoreCase(student.getLastName()) && 
                    firstName.equalsIgnoreCase(student.getFirstName())) {
                    result.add(student);
                }
            }
        }
        return result;
    }
    
    @GetMapping("/filterLastName")
    public List<Student> searchByLastName(@RequestParam String lastName) {
        List<Student> result = new ArrayList<>();
        if (Objects.nonNull(lastName) && Objects.nonNull(students)) {
            for (Student student : students) {
                if (lastName.equalsIgnoreCase(student.getLastName())) {
                    result.add(student);
                }
            }
        }
        return result;
    }

    @GetMapping("/filter")
    public List<Student> searchByName(@RequestParam String name) {
        List<Student> result = new ArrayList<>();
        if (Objects.nonNull(name) && Objects.nonNull(students)) {
            for (Student student : students) {
                if (name.equalsIgnoreCase(student.getFirstName()) || 
                    name.equalsIgnoreCase(student.getLastName())) {
                    result.add(student);
                } else if (name.equalsIgnoreCase(student.getFirstName() + " " + student.getLastName())) {
                    result.add(student);
                }
            }
        }
        return result;
    }
}

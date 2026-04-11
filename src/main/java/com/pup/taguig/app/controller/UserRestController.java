package com.pup.taguig.app.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pup.taguig.app.model.Student;
import com.pup.taguig.app.model.User;
import com.pup.taguig.app.service.UserService;
import com.pup.taguig.app.service.impl.UserServiceImpl;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("user")
public class UserRestController {

    private List<Student> students = null;
    
    List<User> users = new ArrayList<User>();


    private final UserService userService = new UserServiceImpl();
    
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

        for (Student s : students) {
            userService.addUser(s);
        }
    }
    
    @GetMapping("/")
    public List<Student> getAllUsers() {
        return students;
    }
    
    @GetMapping("/{id}")
    public Student getUsersById(@PathVariable Long id) {
        
        if (Objects.nonNull(students)) {
            // for (Student student : students) {
            //     if (student.getId().equals(id)) {
            //         return student;
            //     }
            // }
            return userService.getUserById(id);
        }
        System.out.println(students.size());
        return null;
    }

    @PostMapping("/")
    public Student addStudent(@RequestBody Student student) {
        Student result = null;
        if (Objects.nonNull(student)) {
            result = userService.addUser(student);
        }
        return result;
    }
    
    @GetMapping("/all")
    public List<Student> getAllPosted() {
        return userService.retrieveAllStudent();
    }

    @GetMapping("/filter")
	public List <Student> searchByName(@RequestParam("lastName") String name, 
		@RequestParam String firstName,@RequestParam String midtermGrade, @RequestParam String finalGrade) {
		List <Student> result = new ArrayList<>();
		if (Objects.nonNull(name) && Objects.nonNull(students)) {
			for (Student student: students) {
				if (name.equalsIgnoreCase(student.getLastName()) &&
						student.getFirstName().equalsIgnoreCase(firstName)) {
					result.add(student);
				}
			}
		}
			
		return result;
	}

    @DeleteMapping("/{id}")
    public boolean deleteStudent(@RequestParam("id") Long id) {
        return userService.deleteStudent(id);
    }







    @GetMapping("/filter/full-name")
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
    
    @GetMapping("/filter/last-name")
    public List<Student> searchByLastName(@RequestParam String lastName) {
        List<Student> result = new ArrayList<>();
        if (Objects.nonNull(lastName)) {
            return userService.searchByLastName(lastName);
        }
        return new ArrayList<>();
    }

    
}

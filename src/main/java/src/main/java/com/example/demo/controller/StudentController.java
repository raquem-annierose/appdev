package src.main.java.com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import src.main.java.com.example.model.Student;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final List<Student> students = new ArrayList<>();

    // GET all students
    @GetMapping
    public List<Student> getAllStudents() {
        return students;
    }

    // POST - Create a new student
    @PostMapping
    public Student createStudent(@RequestBody StudentRequest request) {
        Student student = new Student(
            request.getFirstName(),
            request.getLastName(),
            request.getMidtermGrade(),
            request.getFinalGrade()
        );
        students.add(student);
        return student;
    }

    // GET student by index
    @GetMapping("/{index}")
    public Student getStudent(@PathVariable int index) {
        if (index >= 0 && index < students.size()) {
            return students.get(index);
        }
        throw new RuntimeException("Student not found");
    }

    // DELETE student by index
    @DeleteMapping("/{index}")
    public String deleteStudent(@PathVariable int index) {
        if (index >= 0 && index < students.size()) {
            students.remove(index);
            return "Student deleted successfully";
        }
        throw new RuntimeException("Student not found");
    }

    // Request DTO class
    public static class StudentRequest {
        private String firstName;
        private String lastName;
        private float midtermGrade;
        private float finalGrade;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public float getMidtermGrade() { return midtermGrade; }
        public void setMidtermGrade(float midtermGrade) { this.midtermGrade = midtermGrade; }
        
        public float getFinalGrade() { return finalGrade; }
        public void setFinalGrade(float finalGrade) { this.finalGrade = finalGrade; }
    }
}

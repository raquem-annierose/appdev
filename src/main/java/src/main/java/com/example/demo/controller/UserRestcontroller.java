package src.main.java.com.example.demo.controller;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import src.main.java.com.example.model.Student;


@RestController
public class UserRestcontroller {

	
	private List<Student> students = null;
//	
//	@GetMapping("users") public String getAllUsers() {
//		return "Sample";
//	}
	
	@PostConstruct
	public void init () {
		
//		users = new ArrayList<Student>();
		
		students = new ArrayList<Student>();
		
//		 List<Student> students = new ArrayList<Student> ();
	        
	     	Student st1 = new Student((long) 1, "Annie", "Raquem",  85, 90);
	        Student st2 = new Student((long) 2, "Rose", "Raquem",  95, 97);
	        Student st3 = new Student((long) 3, "Yowro", "Raquem",  90, 85);
	        Student st4 = new Student((long) 4, "Ann", "Raquem",  90, 85);
	        Student st5 = new Student((long) 5, "Niera", "Raquem",  90, 85);
	        
	        students.add(st1);
	        students.add(st2);
	        students.add(st3);
	        students.add(st4);
	        students.add(st5);
	      
			
	}
	
	@GetMapping("user")
    public List<Student> getAllUsers() {
		
		return students;
		
	}
	
	@GetMapping("user/{id}")
    public Student getUsersById(@PathVariable Long id) {
		
			if (Objects.nonNull(students)) {
				for (Student student: students) {
//					if (Student.getid().equals(id)) {
//						
//				}
					if (student.getId().equals(id)) {
						return student;
					}
			
					
				}
			}

			System.out.println(students.size());
			return null;
		
	}

	@PostMapping("user")
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
			// return student;
		}
		return null;
		
	}
	
//	
//	@GetMapping("user/id")
//    public Student getUsersById{
//		
//		
//	}
	
	
//	@GetMapping("user")
//      public String retrieveAllUsers () {
//		User user = new User ("Annie", "Rose", 90);
//		return "any string";
//		
//	}
//	
	
	
	
}

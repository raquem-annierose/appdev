package src.main.java.com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

//import org.springframework.web.bind.annotation.GetMapping;
//import src.main.java.com.example.model.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import src.main.java.com.example.model.Student;


@RestController
public class UserController {

	@RequestMapping(value = "user", method = RequestMethod.GET)
	public List<Student> retrieveAllUserss() {
		
        List<Student> students = new ArrayList <> ();
        
        Student st1 = new Student("Annie", "Raquem",  85, 90);
        Student st2 = new Student("Rose", "Raquem",  95, 97);
        Student st3 = new Student("Yowro", "Raquem",  90, 85);
        Student st4 = new Student("Ann", "Raquem",  90, 85);
        Student st5 = new Student("Niera", "Raquem",  90, 85);
        
        students.add(st1);
        students.add(st2);
        students.add(st3);
        students.add(st4);
        students.add(st5);
      
		return students;
		
	}
	
//	@GetMapping("user")
//	public String retrieveAllUsers () {
//		User user = new User ("Annie", "Rose", 90);
//		return "any string";
//		
//	}
	
	
}

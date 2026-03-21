package src.main.java.com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import src.main.java.com.example.model.Calculator;
import src.main.java.com.example.model.Student;

@SpringBootApplication
@ComponentScan(basePackages = "src.main.java.com.example")
public class DemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        
       
    }

    @Override
    public void run(String... args) {
    	int age = 9;
        Calculator calculator = new Calculator(10, 3);
	
		
		System.out.println(calculator.add());
		age = 9;
		System.out.println(calculator.multiply());
		age = 10;
		System.out.println(calculator.subtract());
		age = 11;
		System.out.println(calculator.multiply());
		age = 12;
        
        System.out.println("Hello World");
        
        List<Student> students = new ArrayList <> ();
        
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
        
//        students.add(new Student("Niera", "Raquem",  90, 85));
        
        for (Student student : students) {
        	 System.out.println(student.getFirstName() 
        			 + " " + student.getLastName()
        			 + " " + student.compute()
        			 + " - "
        			 + student.evaluate());
        	 
        }
        
       
        
        
       
    }
    
   

}

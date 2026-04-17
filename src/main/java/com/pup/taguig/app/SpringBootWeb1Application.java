package com.pup.taguig.app;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pup.taguig.app.model.Calculator;
import com.pup.taguig.app.model.Student;

@SpringBootApplication
public class SpringBootWeb1Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootWeb1Application.class, args);
    }

    @Override
    public void run(String... args) {
        Calculator calculator = new Calculator(10, 3);
        
        System.out.println("Add: " + calculator.add());
        System.out.println("Multiply: " + calculator.multiply());
        System.out.println("Subtract: " + calculator.subtract());
        System.out.println("Divide: " + calculator.divide());
        
        System.out.println("Hello World");
        
        List<Student> students = new ArrayList<>();
        
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
        
        for (Student student : students) {
            System.out.println(student.getFirstName() 
                + " " + student.getLastName()
                + " " + student.compute()
                + " - " + student.evaluate());
        }
    }
}

package com.pup.taguig.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity

public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    auto increment id
    private long id;
    
//    @Column(nullable=false, length=100, unique=true)
    @Column(nullable=false, length=100, unique=true)
    private String firstName;
    
    @Column(nullable=false)
    private String lastName;
    
    @Column(nullable=true)
    private float midtermGrade;
    
    @Column(nullable=true)
    private float finalGrade;
    
    public Student() {}

    public Student(String firstName, String lastName, float midtermGrade, float finalGrade) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.midtermGrade = midtermGrade;
        this.finalGrade = finalGrade;
    }

    public Long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public float getMidtermGrade() {
        return midtermGrade;
    }

    public void setMidtermGrade(float midtermGrade) {
        this.midtermGrade = midtermGrade;
    }

    public float getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(float finalGrade) {
        this.finalGrade = finalGrade;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    
    public float compute() {
        return (midtermGrade + finalGrade) / 2;
    }
    
    public String evaluate() {
        float average = this.compute();
        
        if (average >= 75) {
            return "Pass";
        } else {
            return "Failed";
        }
    }
}

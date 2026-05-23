package com.pup.taguig.app.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentM {
    
	private long id;
	private String firstName;
	private String lastName;
	private float midtermGrade;
	private float finalGrade;
	
    private Department department;
//    public StudentM() {
//    }
//	 
//    public StudentM(String firstName, String lastName, float midtermGrade, float finalGrade) {
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.midtermGrade = midtermGrade;
//        this.finalGrade = finalGrade;
//    }
//
//    public Long getId() {
//        return id;
//    }
//    
//    public void setId(long id) {
//        this.id = id;
//    }
//    
//    public float getMidtermGrade() {
//        return midtermGrade;
//    }
//
//    public void setMidtermGrade(float midtermGrade) {
//        this.midtermGrade = midtermGrade;
//    }
//
//    public float getFinalGrade() {
//        return finalGrade;
//    }
//
//    public void setFinalGrade(float finalGrade) {
//        this.finalGrade = finalGrade;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//    
//    public float compute() {
//        return (midtermGrade + finalGrade) / 2;
//    }
//    
//    public String evaluate() {
//        float average = this.compute();
//        
//        if (average >= 75) {
//            return "Pass";
//        } else {
//            return "Failed";
//        }
//    
//}
}

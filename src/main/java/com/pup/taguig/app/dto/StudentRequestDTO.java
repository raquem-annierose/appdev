package com.pup.taguig.app.dto;

public class StudentRequestDTO {
	 private long id;
	 private String firstName;
	 private String lastName;
	 private float midtermGrade;
	 private float finalGrade;
	 
	 public long getId() {
		 return id;
	 }
	 public void setId(long id) {
		 this.id = id;
	 }
	 public String getFirstName() {
		 return firstName;
	 }
	 public void setFirstName(String firstName) {
		 this.firstName = firstName;
	 }
	 public String getLastName() {
		 return lastName;
	 }
	 public void setLastName(String lastName) {
		 this.lastName = lastName;
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
	 
	 

}

package com.pup.taguig.app.model;

public class User {

    private String firstName;
    private String lastName;
    private int age;
    
    public User(String firstName, String lastName, int age) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    @Override
    public String toString() {
        return "User [First Name=" + firstName + ", Last Name=" + lastName + ", Age=" + age + "]";
    }
}

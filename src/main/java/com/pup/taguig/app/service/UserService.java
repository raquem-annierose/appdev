package com.pup.taguig.app.service;

import java.util.List;

import com.pup.taguig.app.model.Student;


public interface UserService {
	
	public Student addUser(Student student);
	public List<Student>retrieveAllStudent();
	public Student getUserById(Long id);
	
}

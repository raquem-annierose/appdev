package com.pup.taguig.app.service;

import java.util.List;

import com.pup.taguig.app.dto.StudentRequestDTO;
import com.pup.taguig.app.dto.StudentResponseDTO;
import com.pup.taguig.app.model.Student;


public interface UserService {
	
	public Long addUser(StudentRequestDTO student);
	public List<StudentResponseDTO>retrieveAllStudent();
	public Student getUserById(Long id);
	public List<StudentResponseDTO> searchByName(String name, String firstName);
	public boolean deleteStudent(Long id);
	
}

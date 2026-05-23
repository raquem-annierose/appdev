package com.pup.taguig.app.service;

import java.util.List;

import com.pup.taguig.app.dto.StudentRequestDTO;
import com.pup.taguig.app.dto.StudentResponseDTO;

public interface StudentService {
	
	
    public StudentResponseDTO getUserById(Long id);
    public List<StudentResponseDTO> retrieveAllStudent();
    public Long insertStudent(StudentRequestDTO student);
    public boolean deleteStudentById(Long id);
    // public StudentResponseDTO updateStudent(Long id, StudentRequestDTO student);
    // public List<StudentResponseDTO> searchByName(String lastName, String firstName);
}



	


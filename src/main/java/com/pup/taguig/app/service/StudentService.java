package com.pup.taguig.app.service;

import java.util.List;

import com.pup.taguig.app.dto.StudentRequestDTO;
import com.pup.taguig.app.dto.StudentResponseDTO;

public interface StudentService {
	
	
    public StudentResponseDTO getUserById(Long id);
    public List<StudentResponseDTO> retrieveAllStudent();
    public Long insertStudent(StudentRequestDTO student);
    
}



	


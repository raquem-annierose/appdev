package com.pup.taguig.app.service;

import java.util.List;

import com.pup.taguig.app.dto.StudentRequestDTO;
import com.pup.taguig.app.dto.StudentResponseDTO;

public interface StudentService {
	
    public StudentResponseDTO getUserById(Long id);
    public List<StudentResponseDTO> retrieveAllStudent();
    public Long insertStudent(StudentRequestDTO student);
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO student);
    public boolean deleteStudent(Long id);
    public List<StudentResponseDTO> searchByName(String lastName, String firstName);
    
}
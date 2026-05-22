package com.pup.taguig.app.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pup.taguig.app.dto.StudentRequestDTO;
import com.pup.taguig.app.dto.StudentResponseDTO;
import com.pup.taguig.app.model.StudentM;
import com.pup.taguig.app.repositoryM.StudentMapper;
import com.pup.taguig.app.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public StudentResponseDTO getUserById(Long id) {
        StudentM student = studentMapper.getUserById(id);
        if (student != null) {
            return new StudentResponseDTO(
                    student.getId(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getMidtermGrade(),
                    student.getFinalGrade()
            );
        }
        return null;
    }

    @Override
    public List<StudentResponseDTO> retrieveAllStudent() {
        List<StudentM> students = studentMapper.retrieveAllStudent();
        List<StudentResponseDTO> response = new ArrayList<>();
        for (StudentM student : students) {
            StudentResponseDTO dto = new StudentResponseDTO(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getMidtermGrade(),
                student.getFinalGrade()
            );
            response.add(dto);
        }
        return response;
    }

	@Override
	public Long insertStudent(StudentRequestDTO request) {
		if (request == null || request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        
		StudentM student = new StudentM();
		student.setFirstName(request.getFirstName().trim());
		student.setLastName(request.getLastName().trim());
		student.setMidtermGrade(request.getMidtermGrade());
		student.setFinalGrade(request.getFinalGrade());

		studentMapper.insertStudent(student);
		return student.getId();
	}

	@Override
	public StudentResponseDTO updateStudent(Long id, StudentRequestDTO request) {
		if (request == null || request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        
		StudentM student = studentMapper.getUserById(id);
		if (student != null) {
			student.setFirstName(request.getFirstName().trim());
			student.setLastName(request.getLastName().trim());
			student.setMidtermGrade(request.getMidtermGrade());
			student.setFinalGrade(request.getFinalGrade());
			
			studentMapper.updateStudent(student);
			return new StudentResponseDTO(
				student.getId(),
				student.getFirstName(),
				student.getLastName(),
				student.getMidtermGrade(),
				student.getFinalGrade()
			);
		}
		return null;
	}

	@Override
	public boolean deleteStudent(Long id) {
		StudentM student = studentMapper.getUserById(id);
		if (student != null) {
			studentMapper.deleteStudent(id);
			return true;
		}
		return false;
	}

	@Override
	public List<StudentResponseDTO> searchByName(String lastName, String firstName) {
		List<StudentM> students = studentMapper.searchByName(lastName, firstName);
		List<StudentResponseDTO> response = new ArrayList<>();
		for (StudentM student : students) {
			StudentResponseDTO dto = new StudentResponseDTO(
				student.getId(),
				student.getFirstName(),
				student.getLastName(),
				student.getMidtermGrade(),
				student.getFinalGrade()
			);
			response.add(dto);
		}
		return response;
	}

}

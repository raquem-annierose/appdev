package com.pup.taguig.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pup.taguig.app.dto.DepartmentResponseDTO;
import com.pup.taguig.app.dto.StudentRequestDTO;
import com.pup.taguig.app.dto.StudentResponseDTO;
import com.pup.taguig.app.model.Student;
import com.pup.taguig.app.model.StudentM;
import com.pup.taguig.app.repository.StudentRepository;
import com.pup.taguig.app.repositoryM.StudentMapper;
import com.pup.taguig.app.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public StudentResponseDTO getUserById(Long id) {
        StudentM student = studentMapper.getUserById(id);
        if (student == null) {
            return null;
        }
        return this.toDTO(student);
    }
    
    private StudentResponseDTO toDTO(StudentM student) {
    	StudentResponseDTO responseDTO = new StudentResponseDTO (
    			 student.getId(),
                 student.getFirstName(),
                 student.getLastName(),
                 student.getMidtermGrade(),
                 student.getFinalGrade()
         );
    			
    	if(student.getDepartment() != null) {
    		DepartmentResponseDTO deptDTO = new DepartmentResponseDTO();
    		deptDTO.setId(student.getDepartment().getId());
    		deptDTO.setName(student.getDepartment().getName());
    		deptDTO.setDisplayName(student.getDepartment().getDisplayName());
    		responseDTO.setDepartment(deptDTO);
    	}
    	return responseDTO;
    }

    @Override
    public List<StudentResponseDTO> retrieveAllStudent() {
        List<StudentM> students = studentMapper.retrieveAllStudent();
        return students.stream()
                .map(this::toDTO)
                .toList();
    }

	@Override
	public Long insertStudent(StudentRequestDTO request) {
		// TODO Auto-generated method stub
		StudentM student = new StudentM();
		student.setFirstName(request.getFirstName());
		student.setLastName(request.getLastName());
		student.setMidtermGrade(request.getMidtermGrade());
		student.setFinalGrade(request.getFinalGrade());

		Long id = studentMapper.insertStudent(student);
		return student.getId();
	}
    
    @Override
    public boolean deleteStudentById(Long id) {
        boolean result = false;

        try {
            if (studentMapper.deleteStudentById(id) > 0) {
            result = true;
            }
        } catch (Exception e) {
            System.out.println("Error deleting student with id " + id + ": " + e.getMessage());
        }

        return result;
    }
    

}

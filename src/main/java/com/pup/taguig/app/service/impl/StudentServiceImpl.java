package com.pup.taguig.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        return new StudentResponseDTO(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getMidtermGrade(),
                student.getFinalGrade()
        );
    }

    @Override
    public List<StudentResponseDTO> retrieveAllStudent() {
       
        List<StudentM> students = studentMapper.retrieveAllStudent();
        return students.stream()
        .map(student -> new StudentResponseDTO(
            student.getId(),
            student.getFirstName(),
            student.getLastName(),
            student.getMidtermGrade(),
            student.getFinalGrade()
        ))
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
    
    

}

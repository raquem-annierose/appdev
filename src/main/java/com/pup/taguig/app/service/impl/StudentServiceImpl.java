package com.pup.taguig.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        return new StudentResponseDTO(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getMidtermGrade(),
                student.getFinalGrade()
        );
    }

    public List<StudentResponseDTO> retrieveAllStudent() {
        // Implementation for retrieving all students
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

}

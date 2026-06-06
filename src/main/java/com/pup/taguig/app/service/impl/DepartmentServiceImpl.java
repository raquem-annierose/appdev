package com.pup.taguig.app.service.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pup.taguig.app.dto.DepartmentResponseDTO;
import com.pup.taguig.app.dto.StudentResponseDTO;
import com.pup.taguig.app.model.Department;
import com.pup.taguig.app.model.StudentM;
import com.pup.taguig.app.repository.DepartmentMapper;
import com.pup.taguig.app.service.DepartmentService;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger logger = Logger.getLogger(DepartmentServiceImpl.class.getName());

    @Autowired
    private DepartmentMapper deptRepository;

    @Override
    public DepartmentResponseDTO getDepartmentAndStudentsById(Long id) {
        DepartmentResponseDTO result = new DepartmentResponseDTO();

        try {
            Department dept = deptRepository.getDepartmentAndStudentsById(id);
           
            result.setName(dept.getName());
            result.setDisplayName(dept.getDisplayName());
            if(Objects.nonNull(dept.getStudents())) {
                List<StudentResponseDTO> studDtoList = new ArrayList<>();
                for(StudentM student : dept.getStudents()) {
                    StudentResponseDTO students = new StudentResponseDTO();
                    students.setId(student.getId());
                    students.setFirstName(student.getFirstName());
                    students.setLastName(student.getLastName());

                    studDtoList.add(students);
                }
                result.setStudents(studDtoList);

            }

        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error retrieving department and students by id", e);
        }
        return result;
   }

}

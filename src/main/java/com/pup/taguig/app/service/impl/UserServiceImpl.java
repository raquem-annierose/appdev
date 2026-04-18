package com.pup.taguig.app.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.RequestParam;

import com.pup.taguig.app.dto.StudentRequestDTO;
import com.pup.taguig.app.dto.StudentResponseDTO;
import com.pup.taguig.app.model.Student;
import com.pup.taguig.app.repository.StudentRepository;
import com.pup.taguig.app.service.UserService;


@Service
public class UserServiceImpl implements UserService {

    private List<StudentResponseDTO> students = new ArrayList<StudentResponseDTO>();
    
    // annotation for calling the repository comoconnect sa database 
    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Long addUser(StudentRequestDTO request) {

//        LocalDateTime date = LocalDateTime.now();
//        long id = date.getDayOfYear() +
//                date.getYear() +
//                date.getMonthValue() +
//                date.getDayOfMonth() +
//                date.getDayOfWeek().getValue() +
//                date.getHour() +
//                date.getMinute() +
//                date.getSecond() +
//                date.getNano();
//        student.setId(id);
//        students.add(student);

        Student student = new Student(
            request.getFirstName(),
            request.getLastName(),
            request.getMidtermGrade(),
            request.getFinalGrade()
        );

        // Student studentEntity = new Student();
        // studentEntity.setFirstName(student.getFirstName());
        // studentEntity.setLastName(student.getLastName());
        // studentEntity.setMidtermGrade(student.getMidtermGrade());
        // studentEntity.setFinalGrade(student.getFinalGrade());
        

        student = studentRepository.save(student);
        // Save to database - this triggers ID auto-generation
        return student.getId();
    }

    @Override
    public List<StudentResponseDTO> retrieveAllStudent() {
    	return students;
    }

    @Override
    public Student getUserById(Long id) {
//        for (Student student : students) {
//            if (student.getId().equals(id)) {
//                return student;
//            }
//        }
        return null;
    }
    
    @Override
	public List<StudentResponseDTO>searchByName(String name, String firstName) {
		List<StudentResponseDTO> result = new ArrayList<StudentResponseDTO>();
//		List<Student> result = new ArrayList<Student>();
////		
////		for (Student student: students) {
////			if (name.equalsIgnoreCase(student.getLastName()) &&
////					student.getFirstName().equalsIgnoreCase(firstName)) {
////				result.add(student);
////			}
////		}
		return result;
	}
    
    @Override
    public boolean deleteStudent(Long id) {
//        for (Student student : students) {
//            if (student.getId().equals(id)) {
//                students.remove(student);
//                return true;
//            }
//        }
        return false;
    }

}


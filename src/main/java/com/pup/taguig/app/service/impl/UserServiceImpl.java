package com.pup.taguig.app.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.RequestParam;

import com.pup.taguig.app.model.Student;
import com.pup.taguig.app.service.UserService;


@Service
public class UserServiceImpl implements UserService {

    private List<Student> students = new ArrayList<Student>();

    @Override
    public Student addUser(Student student) {

        LocalDateTime date = LocalDateTime.now();
        long id = date.getDayOfYear() +
                date.getYear() +
                date.getMonthValue() +
                date.getDayOfMonth() +
                date.getDayOfWeek().getValue() +
                date.getHour() +
                date.getMinute() +
                date.getSecond() +
                date.getNano();
        student.setId(id);
        students.add(student);

    	return student;
    }

    @Override
    public List<Student> retrieveAllStudent() {
    	return students;
    }

    @Override
    public Student getUserById(Long id) {
        for (Student student : students) {
            if (student.getId().equals(id)) {
                return student;
            }
        }
        return null;
    }
    
    @Override
	public List<Student>searchByName(String name, String firstName) {
		List<Student> result = new ArrayList<Student>();
		
		for (Student student: students) {
			if (name.equalsIgnoreCase(student.getLastName()) &&
					student.getFirstName().equalsIgnoreCase(firstName)) {
				result.add(student);
			}
		}
		return result;
	}
    
    @Override
    public boolean deleteStudent(Long id) {
        for (Student student : students) {
            if (student.getId().equals(id)) {
                students.remove(student);
                return true;
            }
        }
        return false;
    }

}


package com.pup.taguig.app.repositoryM;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.pup.taguig.app.model.StudentM;

@Mapper
public interface StudentMapper {
	
	public StudentM getUserById(Long id);
    public List<StudentM> retrieveAllStudent();
	public Long insertStudent(StudentM student);
	public int updateStudent(StudentM student);
	public int deleteStudent(Long id);
	public List<StudentM> searchByName(@Param("lastName") String lastName, @Param("firstName") String firstName);
    
}

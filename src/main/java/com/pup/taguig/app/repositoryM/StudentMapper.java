package com.pup.taguig.app.repositoryM;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

import com.pup.taguig.app.model.StudentM;

@Mapper
public interface StudentMapper {
	
	public StudentM getUserById(Long id);
    public List<StudentM> retrieveAllStudent();
    
}

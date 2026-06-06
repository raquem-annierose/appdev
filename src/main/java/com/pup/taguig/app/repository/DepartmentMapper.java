package com.pup.taguig.app.repository;

import org.apache.ibatis.annotations.Mapper;

import com.pup.taguig.app.model.Department;

@Mapper
public interface DepartmentMapper {

    public Department getDepartmentAndStudentsById(Long id);
    


}

package com.pup.taguig.app.service;

import com.pup.taguig.app.dto.DepartmentResponseDTO;


public interface DepartmentService {

    public DepartmentResponseDTO getDepartmentAndStudentsById(Long id);
    
}

package com.pup.taguig.app.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pup.taguig.app.dto.DepartmentResponseDTO;
import com.pup.taguig.app.service.DepartmentService;

@RestController
@RequestMapping("department")
public class DepartmentController {

	@Autowired
	private DepartmentService departmentService;

	@GetMapping("/{id}")
	public DepartmentResponseDTO getDepartmentAndStudentsById(@PathVariable Long id) {
		DepartmentResponseDTO result = departmentService.getDepartmentAndStudentsById(id);

		if (Objects.nonNull(result)) {
			return result;
		}

		return null;
	}

}

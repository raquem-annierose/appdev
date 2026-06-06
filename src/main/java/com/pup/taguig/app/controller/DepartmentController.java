package com.pup.taguig.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pup.taguig.app.service.DepartmentService;

@RestController
@RequestMapping("department")
public class DepartmentController {
	
	@Autowired 
	private DepartmentService departmentService;

}

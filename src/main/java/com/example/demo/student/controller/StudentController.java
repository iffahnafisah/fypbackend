package com.example.demo.student.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.student.entities.Student;
import com.example.demo.student.model.StudentRequest;
import com.example.demo.student.model.StudentResponse;
import com.example.demo.student.service.StudentService;

@RestController
@RequestMapping("/students")
public class StudentController {

	@Autowired
	private StudentService studentService;
	
	@PostMapping("/create")
	// change here
	public StudentResponse createStudent(@RequestBody StudentRequest request) {
		return studentService.saveStudent(request);
	}
	
	@GetMapping("/list")
	public List<Student> getStudentList() {
		return studentService.getStudentList();
	}
	
}

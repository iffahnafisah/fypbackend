package com.example.demo.student.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.student.entities.Student;
import com.example.demo.student.model.StudentRequest;
import com.example.demo.student.model.StudentResponse;
import com.example.demo.student.repository.StudentRepository;

@Service
public class StudentService {

	@Autowired
	private StudentRepository studentRepository;
	
	// change here
	public StudentResponse saveStudent(StudentRequest request) {
		
		Student student = new Student();
		
		student.setFirstname(request.getFirstname());
		student.setLastname(request.getLastname());
		student.setCourse(request.getCourse());
		
		Student savedStudent = studentRepository.save(student);
		
		// change here
		StudentResponse response = new StudentResponse();
		BeanUtils.copyProperties(savedStudent, response);
		return response;
		
//		return savedStudent.getFirstname() + savedStudent.getLastname();
	}
	
	public List<Student> getStudentList(){
		return studentRepository.findAll();
	}
}

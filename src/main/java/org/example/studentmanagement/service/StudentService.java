package org.example.studentmanagement.service;

import org.example.studentmanagement.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    void registerStudent(Student student);

    List<Student> getAllStudents();

    Optional<Student> getStudentById(int id);
}

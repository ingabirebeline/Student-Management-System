package org.example.studentmanagement.repository;

import org.example.studentmanagement.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository {

    void save(Student student);

    List<Student> findAll();

    Optional<Student> findById(int id);
}

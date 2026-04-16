package com.library.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.management.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    // 🔥 Custom method (search by studentId column)
    Student findByStudentId(String studentId);
}
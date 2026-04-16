package com.library.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.management.entity.IssueBook;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface IssueBookRepository extends JpaRepository<IssueBook, Long> {
// 🔥 YAHAN ADD KARO
   IssueBook findByStudentIdAndBookId(String studentId, String bookId);
  @Query("SELECT i FROM IssueBook i WHERE i.studentId = :studentId")
IssueBook getByStudentId(@Param("studentId") String studentId);
}
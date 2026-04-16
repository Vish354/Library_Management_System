package com.library.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.library.management.entity.ReturnBook;

public interface ReturnBookRepository extends JpaRepository<ReturnBook, Long> {
}
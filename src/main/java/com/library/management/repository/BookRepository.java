package com.library.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.management.entity.Book;


@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    Book findByBookId(String bookId);

}
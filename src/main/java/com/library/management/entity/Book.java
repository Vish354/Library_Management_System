package com.library.management.entity;

import jakarta.persistence.*;

@Entity
public class Book {

   @Id
private String bookId;   // 🔥 PRIMARY KEY
    private int quantity;

    
    private String bookName;
    private String publisher;
    private double price;
    private int publishYear;

    // Getters and Setters

   

    public int getQuantity() { return quantity; }   // ✅ ADD THIS
    public void setQuantity(int quantity) { this.quantity = quantity; }  // ✅ ADD THIS

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getPublishYear() { return publishYear; }
    public void setPublishYear(int publishYear) { this.publishYear = publishYear; }
}
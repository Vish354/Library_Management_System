package com.library.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

@Entity
public class Student {

    @Id   // 🔥 PRIMARY KEY
    private String studentId;

    @Pattern(regexp = "^[A-Za-z ]+$", message = "Only characters allowed!")
    private String studentName;

    private String course;
    private String branch;
    private String semester;

    // ✅ GETTERS & SETTERS

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
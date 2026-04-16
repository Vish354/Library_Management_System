package com.library.management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.library.management.entity.Book;
import com.library.management.entity.IssueBook;
import com.library.management.entity.Student;
import com.library.management.repository.BookRepository;
import com.library.management.repository.IssueBookRepository;
import com.library.management.repository.StudentRepository;
import com.library.management.entity.ReturnBook;
import com.library.management.repository.ReturnBookRepository;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BookController {

    private final StudentRepository studentRepository;
    private final BookRepository bookRepository;
    private final IssueBookRepository issueBookRepository;
    private final ReturnBookRepository returnBookRepository;

    public BookController(StudentRepository studentRepository,
            BookRepository bookRepository,
            IssueBookRepository issueBookRepository,
            ReturnBookRepository returnBookRepository) {
        this.studentRepository = studentRepository;
        this.bookRepository = bookRepository;
        this.issueBookRepository = issueBookRepository;
        this.returnBookRepository = returnBookRepository;
    }

    // ================= LOGIN =================

    @PostMapping("/login")
    public String loginProcess(@RequestParam String username,
            @RequestParam String password,
            Model model,
            jakarta.servlet.http.HttpSession session) {

        if (username.equals("admin") && password.equals("admin123")) {

            // 🔥 LOGIN SUCCESS
            session.setAttribute("user", username);

            return "redirect:/dashboard";
        }

        // ❌ LOGIN FAIL
        model.addAttribute("message", "Invalid username or password!");
        return "login";
    }
    // ================= DASHBOARD =================

    @GetMapping("/dashboard")
    public String dashboardPage(jakarta.servlet.http.HttpSession session) {

        // 🔥 CHECK LOGIN
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        return "dashboard";
    }

    // ================= STUDENT =================

    @GetMapping("/student")
    public String studentForm(Model model, jakarta.servlet.http.HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("student", new Student());
        return "student-form";
    }

    @PostMapping("/saveStudent")
    public String saveStudent(Student student, Model model, RedirectAttributes redirectAttributes) {

        if (!student.getStudentName().matches("^[A-Za-z ]+$")) {
            model.addAttribute("message", "Only characters allowed in name!");
            return "student-form";
        }

        if (studentRepository.existsById(student.getStudentId())) {
            model.addAttribute("message", "Student ID already exists!");
            return "student-form";
        }

        studentRepository.save(student);

        // ✅ message pass during redirect
        redirectAttributes.addFlashAttribute("message", "Registration Successful ✔️");

        return "redirect:/student";
    }

    // ================= BOOK =================

    @GetMapping("/addBook")
    public String showBookForm(Model model, jakarta.servlet.http.HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("book", new Book());
        return "book-form";
    }

    @PostMapping("/saveBook")
    public String saveBook(Book book, Model model) {

        // 🔥 Duplicate check
        if (bookRepository.existsById(book.getBookId())) {
            model.addAttribute("message", "Book ID already exists!");
            return "book-form";
        }

        bookRepository.save(book);
        return "redirect:/dashboard";
    }

    // ================= ISSUE BOOK =================

    @GetMapping("/issue-book")
    public String showIssueBookPage(Model model, jakarta.servlet.http.HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("books", bookRepository.findAll());
        return "issue-book";
    }

    @PostMapping("/issue-book")
    public String issueBook(@RequestParam String studentId,
            @RequestParam String bookId,
            Model model) {

        // 🔥 Empty check
        if (studentId == null || studentId.trim().isEmpty()) {
            model.addAttribute("message", "Enter Student ID!");
            return "issue-book";
        }

        if (bookId == null || bookId.trim().isEmpty()) {
            model.addAttribute("message", "Enter Book ID!");
            return "issue-book";
        }

       Student student = studentRepository.findByStudentId(studentId.trim());
       Book book = bookRepository.findById(bookId.trim()).orElse(null);

        if (student == null) {
            model.addAttribute("message", "Registration not completed!");
        } else if (book == null) {
            model.addAttribute("message", "Book not found!");
        } else if (book.getQuantity() <= 0) {
            model.addAttribute("message", "Book not available!");
        } else {

            book.setQuantity(book.getQuantity() - 1);
            bookRepository.save(book);

            IssueBook issue = new IssueBook();
            issue.setStudentId(student.getStudentId());
            issue.setBookId(book.getBookId());
            issue.setIssueDate(java.time.LocalDate.now());
            issue.setDueDate(java.time.LocalDate.now().plusDays(7));

            issueBookRepository.save(issue);

            model.addAttribute("message", "Book Issued Successfully!");
        }

        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("books", bookRepository.findAll());

        return "issue-book";
    }

    // ================= RETURN BOOK =================
    // ================= RETURN BOOK PAGE =================

    @GetMapping("/return-book")
    public String showReturnPage(jakarta.servlet.http.HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        return "return-book";
    }

    // ================= RETURN BOOK =================

    @PostMapping("/return-book")
    public String returnBook(@RequestParam String studentId,
            @RequestParam String bookId,
            Model model) {

        // 🔥 EMPTY CHECK
        if (studentId == null || studentId.trim().isEmpty() ||
    bookId == null || bookId.trim().isEmpty()) {

            model.addAttribute("message", "Please search student first!");
            return "return-book";
        }

        // 🔥 FIND ISSUE
       IssueBook issue = issueBookRepository
        .findAll()
        .stream()
        .filter(i -> 
    i.getStudentId() != null &&
    i.getBookId() != null &&
    studentId.trim().equals(i.getStudentId()) &&
    bookId.trim().equals(i.getBookId())
)
        .findFirst()
        .orElse(null);

        if (issue == null) {
            model.addAttribute("message", "Invalid student or book!");
            return "return-book";
        }

        // 🔥 SAVE INTO RETURN TABLE
        ReturnBook ret = new ReturnBook();
        ret.setStudentId(issue.getStudentId());
        ret.setBookId(issue.getBookId());
        ret.setIssueDate(issue.getIssueDate());
        ret.setReturnDate(java.time.LocalDate.now());

        returnBookRepository.save(ret);

        // 🔥 UPDATE BOOK QUANTITY
       Book book = bookRepository
        .findById(issue.getBookId().trim())
        .orElse(null);

        if (book != null) {
            book.setQuantity(book.getQuantity() + 1);
            bookRepository.save(book);
        }

        // 🔥 DELETE FROM ISSUE TABLE
        issueBookRepository.delete(issue);

        model.addAttribute("message", "Book Returned Successfully!");
        return "return-book";
    }

    // ================= GET ISSUE (SEARCH API) =================

// 🔥 ROOT URL HANDLE (YAHI ADD KARO)
@GetMapping("/")
public String home() {
    return "redirect:/login";
}

    // logout =============

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout(jakarta.servlet.http.HttpSession session) {

        session.invalidate(); // 🔥 logout

        return "redirect:/login";
    }
    

    // ================= API =================

    @GetMapping("/getBook/{bookId}")
    @ResponseBody
    public Book getBook(@PathVariable String bookId) {
        return bookRepository.findById(bookId.trim()).orElse(null);
    }

   @GetMapping("/getIssue/{studentId}")
@ResponseBody
public IssueBook getIssue(@PathVariable String studentId) {

    return issueBookRepository
            .findAll()
            .stream()
            .filter(i -> 
                i.getStudentId() != null &&
                i.getStudentId().equals(studentId.trim())
            )
            .findFirst()
            .orElse(null);
}

    @GetMapping("/getStudent/{id}")
    @ResponseBody
    public Student getStudent(@PathVariable String id) {
        return studentRepository.findByStudentId(id.trim());
    }
}
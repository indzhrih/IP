package com.example.library.controller;

import com.example.library.model.AppUser;
import com.example.library.model.Book;
import com.example.library.service.BookService;
import com.example.library.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/librarian")
public class LibrarianController {

    private final BookService bookService;
    private final UserService userService;

    public LibrarianController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    @GetMapping("/books")
    public String booksPageGet(Model model,
                               @RequestParam(value = "message", required = false) String message,
                               @RequestParam(value = "error", required = false) String error) {
        return buildBooksPage(model, message, error);
    }

    private String buildBooksPage(Model model, String message, String error) {
        List<Book> books;
        List<AppUser> readers;
        try {
            books = bookService.getAllBooks();
            readers = userService.getReaders();
        } catch (RuntimeException exception) {
            books = Collections.emptyList();
            readers = Collections.emptyList();
            error = exception.getMessage();
        }
        model.addAttribute("books", books);
        model.addAttribute("readers", readers);
        model.addAttribute("message", message);
        model.addAttribute("error", error);
        return "librarian/books";
    }

    @PostMapping("/books/add")
    public String addBook(@RequestParam String title,
                          @RequestParam String author,
                          @RequestParam int year,
                          @RequestParam double price,
                          @RequestParam String category,
                          @RequestParam int totalCopies,
                          @RequestParam int availableCopies,
                          Model model) {
        try {
            if (title.isBlank() || author.isBlank() || category.isBlank()) {
                throw new RuntimeException("Заполните все поля книги.");
            }
            if (totalCopies < 0 || availableCopies < 0 || availableCopies > totalCopies) {
                throw new RuntimeException("Некорректные значения количества экземпляров.");
            }
            Book book = new Book();
            book.setTitle(title.trim());
            book.setAuthor(author.trim());
            book.setYear(year);
            book.setPrice(price);
            book.setCategory(category.trim());
            book.setTotalCopies(totalCopies);
            book.setAvailableCopies(availableCopies);
            bookService.addBook(book);
            return buildBooksPage(model, "Книга добавлена", null);
        } catch (RuntimeException exception) {
            return buildBooksPage(model, null, exception.getMessage());
        }
    }

    @PostMapping("/books/reprice")
    public String reprice(@RequestParam int bookId,
                          @RequestParam double newPrice,
                          Model model) {
        try {
            if (newPrice < 0) {
                throw new RuntimeException("Цена не может быть отрицательной.");
            }
            bookService.updatePrice(bookId, newPrice);
            return buildBooksPage(model, "Цена обновлена", null);
        } catch (RuntimeException exception) {
            return buildBooksPage(model, null, exception.getMessage());
        }
    }

    @PostMapping("/books/issue")
    public String issueBook(@RequestParam int bookId,
                            @RequestParam String readerUsername,
                            Model model) {
        try {
            if (readerUsername == null || readerUsername.isBlank()) {
                throw new RuntimeException("Выберите читателя.");
            }
            bookService.issueBook(bookId);
            userService.addBorrowedBook(readerUsername, bookId, LocalDate.now());
            return buildBooksPage(model, "Книга выдана читателю " + readerUsername, null);
        } catch (RuntimeException exception) {
            return buildBooksPage(model, null, exception.getMessage());
        }
    }

    @PostMapping("/validate-xml")
    public String validateXml(Model model) {
        try {
            bookService.validateXml();
            return buildBooksPage(model, "XML успешно валидирован", null);
        } catch (RuntimeException exception) {
            return buildBooksPage(model, null, exception.getMessage());
        }
    }

    @GetMapping("/readers")
    public String readersPage(Model model,
                              @RequestParam(value = "message", required = false) String message,
                              @RequestParam(value = "error", required = false) String error) {
        List<AppUser> readers;
        List<Book> books;
        try {
            readers = userService.getReaders();
            books = bookService.getAllBooks();
        } catch (RuntimeException exception) {
            readers = Collections.emptyList();
            books = Collections.emptyList();
            error = exception.getMessage();
        }
        model.addAttribute("readers", readers);
        model.addAttribute("books", books);
        model.addAttribute("message", message);
        model.addAttribute("error", error);
        return "librarian/readers";
    }
}

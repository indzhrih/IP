package com.example.library.controller;

import com.example.library.model.AppUser;
import com.example.library.model.Book;
import com.example.library.model.BorrowedBook;
import com.example.library.service.BookService;
import com.example.library.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reader")
public class ReaderController {

    private final BookService bookService;
    private final UserService userService;

    public ReaderController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    @GetMapping("/books")
    public String booksPage(@RequestParam(value = "author", required = false) String author,
                            @RequestParam(value = "year", required = false) Integer year,
                            @RequestParam(value = "category", required = false) String category,
                            Model model) {
        List<Book> books;
        try {
            if ((author == null || author.isBlank())
                    && year == null
                    && (category == null || category.isBlank())) {
                books = bookService.getAllBooks();
            } else {
                books = bookService.searchBooks(author, year, category);
            }
            model.addAttribute("error", null);
        } catch (RuntimeException exception) {
            books = Collections.emptyList();
            model.addAttribute("error", exception.getMessage());
        }
        model.addAttribute("books", books);
        model.addAttribute("author", author);
        model.addAttribute("year", year);
        model.addAttribute("category", category);
        return "reader/books";
    }

    @GetMapping("/account")
    public String accountPage(Model model,
                              @RequestParam(value = "message", required = false) String message,
                              @RequestParam(value = "error", required = false) String error) {
        return buildAccountPage(model, message, error);
    }

    @PostMapping("/account/return")
    public String returnBook(@RequestParam int bookId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            bookService.returnBook(bookId);
            userService.removeBorrowedBook(username, bookId);
            return buildAccountPage(model, "Книга возвращена", null);
        } catch (RuntimeException exception) {
            return buildAccountPage(model, null, exception.getMessage());
        }
    }

    private String buildAccountPage(Model model, String message, String error) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<AppUser> optionalUser = userService.findByUsername(username);
        if (optionalUser.isEmpty()) {
            model.addAttribute("error", "Пользователь не найден: " + username);
            return "reader/account";
        }
        AppUser user = optionalUser.get();
        List<Book> allBooks;
        try {
            allBooks = bookService.getAllBooks();
        } catch (RuntimeException exception) {
            allBooks = Collections.emptyList();
            error = exception.getMessage();
        }
        Map<Integer, Book> bookById = allBooks.stream()
                .collect(Collectors.toMap(Book::getId, b -> b, (a, b) -> a));

        List<Map<String, Object>> borrowedView = new ArrayList<>();
        for (BorrowedBook borrowed : user.getBorrowedBooks()) {
            Map<String, Object> row = new HashMap<>();
            row.put("bookId", borrowed.getBookId());
            row.put("issueDate", borrowed.getIssueDate());
            Book book = bookById.get(borrowed.getBookId());
            row.put("book", book);
            borrowedView.add(row);
        }

        model.addAttribute("user", user);
        model.addAttribute("borrowedBooks", borrowedView);
        model.addAttribute("message", message);
        model.addAttribute("error", error);
        return "reader/account";
    }
}

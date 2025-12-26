package com.example.library.model;

import java.time.LocalDate;

public class BorrowedBook {

    private int bookId;
    private LocalDate issueDate;

    public BorrowedBook() {
    }

    public BorrowedBook(int bookId, LocalDate issueDate) {
        this.bookId = bookId;
        this.issueDate = issueDate;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
}

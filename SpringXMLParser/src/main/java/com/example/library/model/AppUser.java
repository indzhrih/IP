package com.example.library.model;

import java.util.ArrayList;
import java.util.List;

public class AppUser {

    private String username;
    private String password;
    private Role role;
    private List<BorrowedBook> borrowedBooks = new ArrayList<>();

    public AppUser() {
    }

    public AppUser(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public AppUser(String username, String password, Role role, List<BorrowedBook> borrowedBooks) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.borrowedBooks = borrowedBooks;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<BorrowedBook> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(List<BorrowedBook> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }
}

package com.example.library.service;

import com.example.library.model.AppUser;
import com.example.library.model.BorrowedBook;
import com.example.library.model.Role;
import com.example.library.repository.UserXmlRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserXmlRepository userXmlRepository;

    public UserService(UserXmlRepository userXmlRepository) {
        this.userXmlRepository = userXmlRepository;
    }

    public List<AppUser> getAllUsers() {
        return userXmlRepository.loadUsers();
    }

    public List<AppUser> getReaders() {
        return getAllUsers().stream()
                .filter(user -> user.getRole() == Role.READER)
                .collect(Collectors.toList());
    }

    public Optional<AppUser> findByUsername(String username) {
        return getAllUsers().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    public boolean usernameExists(String username) {
        return findByUsername(username).isPresent();
    }

    public void registerReader(String username, String password) {
        if (usernameExists(username)) {
            throw new RuntimeException("Пользователь с таким логином уже существует.");
        }
        AppUser newUser = new AppUser(username, password, Role.READER);
        List<AppUser> all = getAllUsers();
        all.add(newUser);
        userXmlRepository.saveUsers(all);
    }

    public void addBorrowedBook(String username, int bookId, LocalDate issueDate) {
        List<AppUser> all = getAllUsers();
        AppUser user = all.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));
        user.getBorrowedBooks().add(new BorrowedBook(bookId, issueDate));
        userXmlRepository.saveUsers(all);
    }

    public void removeBorrowedBook(String username, int bookId) {
        List<AppUser> all = getAllUsers();
        AppUser user = all.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));

        Iterator<BorrowedBook> iterator = user.getBorrowedBooks().iterator();
        boolean removed = false;
        while (iterator.hasNext()) {
            BorrowedBook borrowedBook = iterator.next();
            if (borrowedBook.getBookId() == bookId) {
                iterator.remove();
                removed = true;
                break;
            }
        }
        if (!removed) {
            throw new RuntimeException("У пользователя нет выданной книги с id=" + bookId);
        }
        userXmlRepository.saveUsers(all);
    }
}

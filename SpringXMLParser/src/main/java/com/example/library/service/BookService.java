package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.repository.LibraryXmlRepository;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class BookService {

    private final LibraryXmlRepository libraryXmlRepository;

    public BookService(LibraryXmlRepository libraryXmlRepository) {
        this.libraryXmlRepository = libraryXmlRepository;
    }

    public List<Book> getAllBooks() {
        try {
            List<Book> books = libraryXmlRepository.loadBooks();
            books.sort(Comparator.comparingInt(Book::getId));
            return books;
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка чтения XML с книгами: " + exception.getMessage(), exception);
        } catch (SAXException exception) {
            throw new RuntimeException("Ошибка валидации XML с книгами: " + exception.getMessage(), exception);
        } catch (ParserConfigurationException exception) {
            throw new RuntimeException("Ошибка разбора XML с книгами: " + exception.getMessage(), exception);
        }
    }

    public List<Book> searchBooks(String author, Integer year, String category) {
        List<Book> all = getAllBooks();
        try {
            return libraryXmlRepository.searchBooksUsingXPath(all, author, year, category);
        } catch (ParserConfigurationException exception) {
            throw new RuntimeException("Ошибка поиска по XPath: " + exception.getMessage(), exception);
        }
    }

    public void addBook(Book newBook) {
        List<Book> books = new ArrayList<>(getAllBooks());
        int nextId = books.stream()
                .mapToInt(Book::getId)
                .max()
                .orElse(0) + 1;
        newBook.setId(nextId);
        books.add(newBook);
        saveBooks(books);
    }

    public void updatePrice(int bookId, double newPrice) {
        List<Book> books = new ArrayList<>(getAllBooks());
        Book target = books.stream()
                .filter(b -> b.getId() == bookId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Книга с id=" + bookId + " не найдена"));
        target.setPrice(newPrice);
        saveBooks(books);
    }

    public void issueBook(int bookId) {
        List<Book> books = new ArrayList<>(getAllBooks());
        Book target = books.stream()
                .filter(b -> b.getId() == bookId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Книга с id=" + bookId + " не найдена"));
        if (target.getAvailableCopies() <= 0) {
            throw new RuntimeException("Нет свободных экземпляров для книги id=" + bookId);
        }
        target.setAvailableCopies(target.getAvailableCopies() - 1);
        saveBooks(books);
    }

    public void returnBook(int bookId) {
        List<Book> books = new ArrayList<>(getAllBooks());
        Book target = books.stream()
                .filter(b -> b.getId() == bookId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Книга с id=" + bookId + " не найдена"));
        if (target.getAvailableCopies() >= target.getTotalCopies()) {
            throw new RuntimeException("Нельзя вернуть книгу: все экземпляры уже в наличии.");
        }
        target.setAvailableCopies(target.getAvailableCopies() + 1);
        saveBooks(books);
    }

    public void validateXml() {
        try {
            libraryXmlRepository.validateCurrentXml();
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка доступа к XML с книгами: " + exception.getMessage(), exception);
        } catch (SAXException exception) {
            throw new RuntimeException("XML с книгами не соответствует XSD: " + exception.getMessage(), exception);
        }
    }

    private void saveBooks(List<Book> books) {
        try {
            libraryXmlRepository.saveBooks(books);
        } catch (ParserConfigurationException exception) {
            throw new RuntimeException("Ошибка формирования XML с книгами: " + exception.getMessage(), exception);
        } catch (TransformerException exception) {
            throw new RuntimeException("Ошибка записи XML с книгами: " + exception.getMessage(), exception);
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка доступа к файлу XML с книгами: " + exception.getMessage(), exception);
        } catch (SAXException exception) {
            throw new RuntimeException("XML с книгами не прошёл валидацию: " + exception.getMessage(), exception);
        }
    }
}

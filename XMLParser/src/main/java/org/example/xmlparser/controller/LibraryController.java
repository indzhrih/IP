package org.example.xmlparser.controller;

import org.example.xmlparser.model.Book;
import org.example.xmlparser.repository.LibraryXmlRepository;
import org.example.xmlparser.view.LibraryView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class LibraryController {

    private final Stage primaryStage;
    private final LibraryView view;
    private final LibraryXmlRepository repository;

    private final ObservableList<Book> masterBookList = FXCollections.observableArrayList();
    private FilteredList<Book> filteredBookList;
    private SortedList<Book> sortedBookList;

    private Path currentXmlPath;
    private Path currentXsdPath;

    public LibraryController(Stage primaryStage, LibraryView view) {
        this.primaryStage = primaryStage;
        this.view = view;
        this.repository = new LibraryXmlRepository();
    }

    public void initialize() {
        configureTableBinding();
        configureMenuHandlers();
        configureButtonHandlers();
        updateStatus("Файл не загружен");
    }

    private void configureTableBinding() {
        filteredBookList = new FilteredList<>(masterBookList, book -> true);
        sortedBookList = new SortedList<>(filteredBookList);
        sortedBookList.setComparator(Comparator.comparingInt(Book::getId));
        view.getBookTableView().setItems(sortedBookList);
    }

    private void configureMenuHandlers() {
        view.getOpenXmlMenuItem().setOnAction(event -> handleOpenXml());
        view.getSaveMenuItem().setOnAction(event -> handleSave(false));
        view.getSaveAsMenuItem().setOnAction(event -> handleSave(true));
        view.getExitMenuItem().setOnAction(event -> primaryStage.close());
    }

    private void configureButtonHandlers() {
        view.getApplyFilterButton().setOnAction(event -> rebuildFilterPredicate());
        view.getResetFilterButton().setOnAction(event -> handleResetFilter());
        view.getAddBookButton().setOnAction(event -> handleAddBook());
        view.getRepriceSelectedButton().setOnAction(event -> handleRepriceSelected());
        view.getIssueBookButton().setOnAction(event -> handleIssueBook());
        view.getValidateXmlButton().setOnAction(event -> handleValidateXml());
    }

    private void handleOpenXml() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть XML-файл библиотеки");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML файлы", "*.xml")
        );

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file == null) {
            return;
        }

        Path xmlPath = file.toPath();
        Path xsdPath = xmlPath.getParent().resolve("library.xsd");

        if (!xsdPath.toFile().exists()) {
            showError("Схема не найдена",
                    "В той же папке, что и XML, должен находиться файл library.xsd.\n" +
                            "Ожидался путь: " + xsdPath,
                    null);
            return;
        }

        try {
            List<Book> books = repository.loadBooks(xmlPath, xsdPath);
            masterBookList.setAll(books);
            currentXmlPath = xmlPath;
            currentXsdPath = xsdPath;
            filteredBookList.setPredicate(book -> true);
            updateStatus("Открыт файл: " + xmlPath.getFileName() + " (" + books.size() + " книг)");
        } catch (IOException exception) {
            showError("Ошибка чтения XML",
                    "Не удалось прочитать файл: " + exception.getMessage(),
                    exception);
        } catch (SAXException exception) {
            showError("Ошибка валидации XML",
                    exception.getMessage(),
                    exception);
        } catch (ParserConfigurationException exception) {
            showError("Ошибка XML",
                    "Не удалось разобрать XML-документ: " + exception.getMessage(),
                    exception);
        }
    }

    private void handleSave(boolean saveAs) {
        if (masterBookList.isEmpty()) {
            showInformation("Нет данных", "Нет книг для сохранения.");
            return;
        }

        if (currentXmlPath == null || currentXsdPath == null || saveAs) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить XML-файл библиотеки");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("XML файлы", "*.xml")
            );
            if (currentXmlPath != null) {
                fileChooser.setInitialDirectory(currentXmlPath.getParent().toFile());
                fileChooser.setInitialFileName(currentXmlPath.getFileName().toString());
            } else {
                fileChooser.setInitialFileName("library.xml");
            }

            File file = fileChooser.showSaveDialog(primaryStage);
            if (file == null) {
                return;
            }

            currentXmlPath = file.toPath();
            currentXsdPath = currentXmlPath.getParent().resolve("library.xsd");
        }

        if (!currentXsdPath.toFile().exists()) {
            showError("Схема не найдена",
                    "Для сохранения требуется файл схемы library.xsd в папке:\n" + currentXsdPath.getParent(),
                    null);
            return;
        }

        try {
            repository.saveBooks(currentXmlPath, currentXsdPath, masterBookList);
            updateStatus("Файл сохранён: " + currentXmlPath.getFileName());
        } catch (ParserConfigurationException exception) {
            showError("Ошибка XML", "Не удалось сформировать XML-документ: " + exception.getMessage(), exception);
        } catch (TransformerException exception) {
            showError("Ошибка сохранения XML", "Не удалось записать файл: " + exception.getMessage(), exception);
        } catch (IOException exception) {
            showError("Ошибка ввода-вывода", "Ошибка при работе с файлом: " + exception.getMessage(), exception);
        } catch (SAXException exception) {
            showError("Ошибка валидации XML после сохранения", exception.getMessage(), exception);
        }
    }

    private void rebuildFilterPredicate() {
        String author = view.getAuthorFilterTextField().getText();
        String category = view.getCategoryFilterTextField().getText();

        Integer year = null;
        String yearText = view.getYearFilterTextField().getText().trim();
        if (!yearText.isEmpty()) {
            try {
                year = Integer.parseInt(yearText);
            } catch (NumberFormatException exception) {
                showInformation("Некорректный год", "Год должен быть целым числом.");
                return;
            }
        }

        try {
            List<Book> matched = repository.searchBooksUsingXPath(masterBookList, author, year, category);
            Set<Integer> matchedIds = matched.stream()
                    .map(Book::getId)
                    .collect(Collectors.toSet());
            filteredBookList.setPredicate(book -> matchedIds.contains(book.getId()));
            updateStatusWithCount();
        } catch (ParserConfigurationException exception) {
            showError("Ошибка поиска", "Не удалось выполнить поиск по XPath: " + exception.getMessage(), exception);
        }
    }

    private void handleResetFilter() {
        view.getAuthorFilterTextField().clear();
        view.getYearFilterTextField().clear();
        view.getCategoryFilterTextField().clear();
        filteredBookList.setPredicate(book -> true);
        updateStatusWithCount();
    }

    private void handleAddBook() {
        String title = view.getNewTitleTextField().getText().trim();
        String author = view.getNewAuthorTextField().getText().trim();
        String yearText = view.getNewYearTextField().getText().trim();
        String priceText = view.getNewPriceTextField().getText().trim();
        String category = view.getNewCategoryTextField().getText().trim();
        String totalText = view.getNewTotalCopiesTextField().getText().trim();
        String availableText = view.getNewAvailableCopiesTextField().getText().trim();

        if (title.isEmpty() || author.isEmpty() || yearText.isEmpty()
                || priceText.isEmpty() || category.isEmpty()
                || totalText.isEmpty() || availableText.isEmpty()) {
            showInformation("Недостаточно данных", "Заполните все поля для новой книги.");
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearText);
        } catch (NumberFormatException exception) {
            showInformation("Некорректный год", "Год должен быть целым числом.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText.replace(',', '.'));
        } catch (NumberFormatException exception) {
            showInformation("Некорректная цена", "Цена должна быть числом, например 49.99.");
            return;
        }

        int totalCopies;
        int availableCopies;
        try {
            totalCopies = Integer.parseInt(totalText);
            availableCopies = Integer.parseInt(availableText);
        } catch (NumberFormatException exception) {
            showInformation("Некорректное количество",
                    "Количество экземпляров должно быть целым числом.");
            return;
        }

        if (totalCopies < 0 || availableCopies < 0) {
            showInformation("Некорректное количество",
                    "Количество экземпляров не может быть отрицательным.");
            return;
        }
        if (availableCopies > totalCopies) {
            showInformation("Некорректное количество",
                    "В наличии не может быть больше, чем всего экземпляров.");
            return;
        }

        int nextId = masterBookList.stream()
                .mapToInt(Book::getId)
                .max()
                .orElse(0) + 1;

        Book book = new Book(nextId, title, author, year, price, category, totalCopies, availableCopies);
        masterBookList.add(book);
        clearAddBookForm();
        updateStatusWithCount();
        saveCurrentBooks();
    }

    private void clearAddBookForm() {
        view.getNewTitleTextField().clear();
        view.getNewAuthorTextField().clear();
        view.getNewYearTextField().clear();
        view.getNewPriceTextField().clear();
        view.getNewCategoryTextField().clear();
        view.getNewTotalCopiesTextField().clear();
        view.getNewAvailableCopiesTextField().clear();
    }

    private void handleRepriceSelected() {
        Book selected = view.getBookTableView().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInformation("Нет выбранной книги", "Выберите книгу в таблице.");
            return;
        }

        String priceText = view.getNewPriceForSelectedTextField().getText().trim();
        if (priceText.isEmpty()) {
            showInformation("Нет значения", "Введите новую цену.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText.replace(',', '.'));
        } catch (NumberFormatException exception) {
            showInformation("Некорректная цена", "Цена должна быть числом, например 39.99.");
            return;
        }

        if (price < 0) {
            showInformation("Некорректная цена", "Цена не может быть отрицательной.");
            return;
        }

        selected.setPrice(price);
        view.getBookTableView().refresh();
        view.getNewPriceForSelectedTextField().clear();
        saveCurrentBooks();
    }

    private void handleIssueBook() {
        Book selected = view.getBookTableView().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInformation("Нет выбранной книги", "Выберите книгу, которую нужно выдать.");
            return;
        }

        if (selected.getAvailableCopies() <= 0) {
            showInformation("Нет в наличии",
                    "У выбранной книги нет свободных экземпляров для выдачи.");
            return;
        }

        selected.setAvailableCopies(selected.getAvailableCopies() - 1);
        view.getBookTableView().refresh();
        saveCurrentBooks();
    }

    private void handleValidateXml() {
        if (currentXmlPath == null || currentXsdPath == null) {
            showInformation("Файл не выбран",
                    "Сначала откройте XML-файл через меню File → Open XML...");
            return;
        }

        try {
            repository.validateXml(currentXmlPath, currentXsdPath);
            showInformation("Валидация успешна", "XML-файл соответствует XSD-схеме.");
        } catch (IOException exception) {
            showError("Ошибка доступа к файлам", exception.getMessage(), exception);
        } catch (SAXException exception) {
            showError("Ошибка валидации XML", exception.getMessage(), exception);
        }
    }

    private void saveCurrentBooks() {
        if (currentXmlPath == null || currentXsdPath == null) {
            showInformation("Файл не выбран",
                    "Сначала откройте XML-файл через меню File → Open XML...");
            return;
        }
        try {
            repository.saveBooks(currentXmlPath, currentXsdPath, masterBookList);
            updateStatus("Файл сохранён: " + currentXmlPath.getFileName());
        } catch (ParserConfigurationException exception) {
            showError("Ошибка XML", "Не удалось сформировать XML-документ: " + exception.getMessage(), exception);
        } catch (TransformerException exception) {
            showError("Ошибка сохранения XML", "Не удалось записать файл: " + exception.getMessage(), exception);
        } catch (IOException exception) {
            showError("Ошибка ввода-вывода", "Ошибка при работе с файлом: " + exception.getMessage(), exception);
        } catch (SAXException exception) {
            showError("Ошибка валидации XML после сохранения", exception.getMessage(), exception);
        }
    }

    private void updateStatusWithCount() {
        int total = masterBookList.size();
        int visible = filteredBookList.size();
        String filePart = currentXmlPath != null ? currentXmlPath.getFileName().toString() : "файл не загружен";
        updateStatus(String.format("%s | книг всего: %d, отображается: %d", filePart, total, visible));
    }

    private void updateStatus(String text) {
        view.getStatusLabel().setText(text);
    }

    private void showError(String title, String message, Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(primaryStage);
        alert.setTitle(title);
        alert.setHeaderText(title);
        String content = Optional.ofNullable(message).orElse("");
        if (exception != null && exception.getMessage() != null) {
            content += "\n\n" + exception.getClass().getSimpleName() + ": " + exception.getMessage();
        }
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(primaryStage);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

package main.java.com.example.studentmanagement.controller;

import main.java.com.example.studentmanagement.model.Student;
import main.java.com.example.studentmanagement.model.StudentFilterCriteria;
import main.java.com.example.studentmanagement.model.StudentSortOption;
import main.java.com.example.studentmanagement.model.StudentStatistics;
import main.java.com.example.studentmanagement.repository.StudentFileRepository;
import main.java.com.example.studentmanagement.service.StudentFilterService;
import main.java.com.example.studentmanagement.service.StudentStatisticsService;
import main.java.com.example.studentmanagement.view.StudentView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class StudentController {

    private final Stage primaryStage;
    private final StudentView studentView;
    private final StudentFileRepository studentFileRepository;
    private final StudentFilterService studentFilterService;
    private final StudentStatisticsService studentStatisticsService;

    private final ObservableList<Student> masterStudentList = FXCollections.observableArrayList();
    private FilteredList<Student> filteredStudentList;
    private SortedList<Student> sortedStudentList;

    public StudentController(
            Stage primaryStage,
            StudentView studentView,
            StudentFileRepository studentFileRepository,
            StudentFilterService studentFilterService,
            StudentStatisticsService studentStatisticsService
    ) {
        this.primaryStage = primaryStage;
        this.studentView = studentView;
        this.studentFileRepository = studentFileRepository;
        this.studentFilterService = studentFilterService;
        this.studentStatisticsService = studentStatisticsService;
    }

    public void initialize() {
        configureTableBinding();
        configureEventHandlers();
        configureTableRowFactory();
        updateStatisticsLabel();
    }

    private void configureTableBinding() {
        filteredStudentList = new FilteredList<>(masterStudentList, student -> true);
        sortedStudentList = new SortedList<>(filteredStudentList);

        sortedStudentList.comparatorProperty().bind(
                studentView.getStudentTableView().comparatorProperty()
        );

        studentView.getStudentTableView().setItems(sortedStudentList);
    }

    private void configureTableRowFactory() {
        studentView.getStudentTableView().setRowFactory(tv -> new TableRow<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);

                if (empty || student == null) {
                    setStyle("");
                    return;
                }

                if (studentFilterService.isExcellentStudent(student)) {
                    if (isSelected()) {
                        setStyle("-fx-background-color: #a3d9b1; -fx-font-weight: bold;");
                    }
                    else {
                        setStyle("-fx-background-color: #d4edda; -fx-font-weight: normal;");
                    }
                }
                else if (student.hasDebt()) {
                    if (isSelected()) {
                        setStyle("-fx-background-color: #e9b7b7; -fx-font-weight: bold;");
                    }
                    else {
                        setStyle("-fx-background-color: #f8d7da; -fx-font-weight: normal;");
                    }
                }
                else {
                    if (isSelected()) {
                        setStyle("-fx-background-color: #cce5ff; -fx-font-weight: bold;");
                    }
                    else {
                        setStyle("-fx-font-weight: normal;");
                    }
                }
            }
        });
    }

    private void configureEventHandlers() {
        studentView.getOpenFileButton().setOnAction(event -> handleOpenFile());
        studentView.getExportVisibleStudentsButton().setOnAction(event -> handleExportVisibleStudents());
        studentView.getResetFiltersButton().setOnAction(event -> handleResetFilters());

        studentView.getShowOnlyExcellentStudentsCheckBox().selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    rebuildFilterPredicate();
                    updateStatisticsLabel();
                });

        studentView.getShowOnlyStudentsWithDebtCheckBox().selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    rebuildFilterPredicate();
                    updateStatisticsLabel();
                });

        studentView.getFullNameFilterTextField().textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    rebuildFilterPredicate();
                    updateStatisticsLabel();
                });

        studentView.getGroupNameFilterTextField().textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    rebuildFilterPredicate();
                    updateStatisticsLabel();
                });

        studentView.getCourseFilterComboBox().valueProperty()
                .addListener((observable, oldValue, newValue) -> {
                    rebuildFilterPredicate();
                    updateStatisticsLabel();
                });

        studentView.getSortOptionComboBox().valueProperty()
                .addListener((observable, oldValue, newValue) -> applySort());

        studentView.getSortDescendingCheckBox().selectedProperty()
                .addListener((observable, oldValue, newValue) -> applySort());
    }

    private void handleOpenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите текстовый файл со студентами");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt")
        );

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file == null) {
            return;
        }

        try {
            List<Student> students = studentFileRepository.loadStudentsFromFile(file.toPath());
            masterStudentList.setAll(students);
            rebuildFilterPredicate();
            updateStatisticsLabel();
            applySort();

        }
        catch (IllegalArgumentException exception) {
            showErrorAlert(
                    "Ошибка формата файла",
                    "Не удалось разобрать файл: " + exception.getMessage(),
                    exception
            );
        }
        catch (IOException exception) {
            showErrorAlert(
                    "Ошибка чтения файла",
                    "Произошла ошибка при чтении файла: " + exception.getMessage(),
                    exception
            );
        }
    }

    private void handleExportVisibleStudents() {
        if (filteredStudentList == null || filteredStudentList.isEmpty()) {
            showInformationAlert("Экспорт невозможен", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить видимый список студентов");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt")
        );
        fileChooser.setInitialFileName("students_export.txt");

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file == null) {
            return;
        }

        List<Student> studentsToSave = filteredStudentList.stream()
                .collect(Collectors.toList());

        try {
            Path filePath = file.toPath();
            studentFileRepository.saveStudentsToFile(filePath, studentsToSave);
            showInformationAlert("Экспорт завершён", "Файл успешно сохранён:\n" + filePath);
        } catch (IOException exception) {
            showErrorAlert(
                    "Ошибка записи файла",
                    "Не удалось сохранить файл: " + exception.getMessage(),
                    exception
            );
        }
    }

    private void handleResetFilters() {
        studentView.getShowOnlyExcellentStudentsCheckBox().setSelected(false);
        studentView.getShowOnlyStudentsWithDebtCheckBox().setSelected(false);
        studentView.getFullNameFilterTextField().clear();
        studentView.getGroupNameFilterTextField().clear();
        studentView.getCourseFilterComboBox().getSelectionModel().selectFirst();
        studentView.getSortOptionComboBox().getSelectionModel()
                .select(StudentSortOption.BY_SEQUENCE_NUMBER);
        studentView.getSortDescendingCheckBox().setSelected(false);

        rebuildFilterPredicate();
        updateStatisticsLabel();
        applySort();
        studentView.getStudentTableView().getSelectionModel().clearSelection();
    }

    private void rebuildFilterPredicate() {
        StudentFilterCriteria criteria = new StudentFilterCriteria();

        criteria.setShowOnlyExcellentStudents(
                studentView.getShowOnlyExcellentStudentsCheckBox().isSelected()
        );
        criteria.setShowOnlyStudentsWithDebt(
                studentView.getShowOnlyStudentsWithDebtCheckBox().isSelected()
        );
        criteria.setFullNameContains(
                studentView.getFullNameFilterTextField().getText()
        );
        criteria.setGroupNameContains(
                studentView.getGroupNameFilterTextField().getText()
        );

        String selectedCourse = studentView.getCourseFilterComboBox()
                .getSelectionModel().getSelectedItem();
        if (selectedCourse != null && !"Все".equals(selectedCourse)) {
            try {
                int course = Integer.parseInt(selectedCourse);
                criteria.setCourseEquals(course);
            } catch (NumberFormatException ignored) {
                criteria.setCourseEquals(null);
            }
        } else {
            criteria.setCourseEquals(null);
        }

        if (filteredStudentList != null) {
            filteredStudentList.setPredicate(
                    studentFilterService.buildFilterPredicate(criteria)
            );
        }
    }

    private void applySort() {
        StudentSortOption sortOption = studentView.getSortOptionComboBox()
                .getSelectionModel().getSelectedItem();

        boolean descending = studentView.getSortDescendingCheckBox().isSelected();

        if (sortOption == null) {
            return;
        }

        TableColumn<Student, ?> columnToSort;
        switch (sortOption) {
            case BY_FULL_NAME -> columnToSort = studentView.getFullNameColumn();
            case BY_GRADE_BOOK_NUMBER -> columnToSort = studentView.getGradeBookNumberColumn();
            case BY_AVERAGE_GRADE -> columnToSort = studentView.getAverageGradeColumn();
            case BY_SEQUENCE_NUMBER -> columnToSort = studentView.getSequenceNumberColumn();
            default -> columnToSort = studentView.getSequenceNumberColumn();
        }

        if (columnToSort == null) {
            return;
        }

        columnToSort.setSortType(
                descending ? TableColumn.SortType.DESCENDING : TableColumn.SortType.ASCENDING
        );

        studentView.getStudentTableView().getSortOrder().clear();
        studentView.getStudentTableView().getSortOrder().add(columnToSort);
        studentView.getStudentTableView().sort();
    }

    private void updateStatisticsLabel() {
        List<Student> currentStudents;

        if (filteredStudentList != null) {
            currentStudents = filteredStudentList.stream().collect(Collectors.toList());
        } else {
            currentStudents = masterStudentList.stream().collect(Collectors.toList());
        }

        StudentStatistics statistics = studentStatisticsService.calculateStatistics(currentStudents);

        String statisticsText = String.format(
                "Всего: %d | Отличников: %d | Должников: %d | Средний балл: %.2f",
                statistics.getTotalStudents(),
                statistics.getExcellentStudents(),
                statistics.getStudentsWithDebt(),
                statistics.getAverageGrade()
        );

        studentView.getStatisticsLabel().setText(statisticsText);
    }

    private void showErrorAlert(String title, String message, Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(primaryStage);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message + "\n\n" + exception.getClass().getSimpleName());
        alert.showAndWait();
    }

    private void showInformationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(primaryStage);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
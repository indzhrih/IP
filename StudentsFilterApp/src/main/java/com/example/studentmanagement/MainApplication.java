package main.java.com.example.studentmanagement;

import main.java.com.example.studentmanagement.controller.StudentController;
import main.java.com.example.studentmanagement.repository.StudentFileRepository;
import main.java.com.example.studentmanagement.service.StudentFilterService;
import main.java.com.example.studentmanagement.service.StudentStatisticsService;
import main.java.com.example.studentmanagement.view.StudentView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        StudentFileRepository studentFileRepository = new StudentFileRepository();
        StudentFilterService studentFilterService = new StudentFilterService();
        StudentStatisticsService studentStatisticsService =
                new StudentStatisticsService(studentFilterService);

        StudentView studentView = new StudentView();
        StudentController studentController = new StudentController(
                primaryStage,
                studentView,
                studentFileRepository,
                studentFilterService,
                studentStatisticsService
        );

        studentController.initialize();

        Scene scene = new Scene(studentView.getRoot(), 1100, 650);
        primaryStage.setTitle("Учет студентов (txt-файлы)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

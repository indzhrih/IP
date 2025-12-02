package org.example.sortapp;

import org.example.sortapp.controller.SortingController;
import org.example.sortapp.view.SortingView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        SortingView sortingView = new SortingView();
        SortingController sortingController = new SortingController(primaryStage, sortingView);
        sortingController.initialize();

        Scene scene = new Scene(sortingView.getRoot(), 1200, 700);
        primaryStage.setTitle("Визуализация сортировок");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

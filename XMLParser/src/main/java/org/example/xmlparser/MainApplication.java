package org.example.xmlparser;

import org.example.xmlparser.controller.LibraryController;
import org.example.xmlparser.view.LibraryView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        LibraryView libraryView = new LibraryView();
        LibraryController libraryController = new LibraryController(primaryStage, libraryView);
        libraryController.initialize();

        Scene scene = new Scene(libraryView.getRoot(), 1100, 650);
        primaryStage.setTitle("Библиотека (XML)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

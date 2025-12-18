package org.example.xmlparser.view;

import org.example.xmlparser.model.Book;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class LibraryView {

    private final BorderPane root;

    private final TableView<Book> bookTableView;
    private final TableColumn<Book, Number> idColumn;
    private final TableColumn<Book, String> titleColumn;
    private final TableColumn<Book, String> authorColumn;
    private final TableColumn<Book, Number> yearColumn;
    private final TableColumn<Book, Number> priceColumn;
    private final TableColumn<Book, String> categoryColumn;
    private final TableColumn<Book, Number> totalCopiesColumn;
    private final TableColumn<Book, Number> availableCopiesColumn;

    private final MenuItem openXmlMenuItem;
    private final MenuItem saveMenuItem;
    private final MenuItem saveAsMenuItem;
    private final MenuItem exitMenuItem;

    private final TextField authorFilterTextField;
    private final TextField yearFilterTextField;
    private final TextField categoryFilterTextField;
    private final Button applyFilterButton;
    private final Button resetFilterButton;

    private final TextField newTitleTextField;
    private final TextField newAuthorTextField;
    private final TextField newYearTextField;
    private final TextField newPriceTextField;
    private final TextField newCategoryTextField;
    private final TextField newTotalCopiesTextField;
    private final TextField newAvailableCopiesTextField;
    private final Button addBookButton;

    private final TextField newPriceForSelectedTextField;
    private final Button repriceSelectedButton;

    private final Button issueBookButton;
    private final Button validateXmlButton;

    private final Label statusLabel;

    public LibraryView() {
        root = new BorderPane();

        bookTableView = new TableView<>();
        idColumn = new TableColumn<>("ID");
        titleColumn = new TableColumn<>("Название");
        authorColumn = new TableColumn<>("Автор");
        yearColumn = new TableColumn<>("Год");
        priceColumn = new TableColumn<>("Цена");
        categoryColumn = new TableColumn<>("Категория");
        totalCopiesColumn = new TableColumn<>("Всего");
        availableCopiesColumn = new TableColumn<>("В наличии");

        configureTable();

        Menu fileMenu = new Menu("File");
        openXmlMenuItem = new MenuItem("Open XML...");
        saveMenuItem = new MenuItem("Save");
        saveAsMenuItem = new MenuItem("Save As...");
        exitMenuItem = new MenuItem("Exit");
        fileMenu.getItems().addAll(openXmlMenuItem, saveMenuItem, saveAsMenuItem, new SeparatorMenuItem(), exitMenuItem);

        MenuBar menuBar = new MenuBar(fileMenu);
        VBox topBox = new VBox(menuBar);
        root.setTop(topBox);

        authorFilterTextField = new TextField();
        yearFilterTextField = new TextField();
        categoryFilterTextField = new TextField();
        applyFilterButton = new Button("Применить фильтр");
        resetFilterButton = new Button("Сбросить фильтр");

        newTitleTextField = new TextField();
        newAuthorTextField = new TextField();
        newYearTextField = new TextField();
        newPriceTextField = new TextField();
        newCategoryTextField = new TextField();
        newTotalCopiesTextField = new TextField();
        newAvailableCopiesTextField = new TextField();
        addBookButton = new Button("Добавить книгу");

        newPriceForSelectedTextField = new TextField();
        repriceSelectedButton = new Button("Изменить цену выбранной книги");

        issueBookButton = new Button("Выдать книгу");
        validateXmlButton = new Button("Валидировать XML");

        statusLabel = new Label("Файл не загружен");

        configureCenter();
        configureRightPanel();
        configureBottom();
    }

    private void configureTable() {
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()));
        titleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));
        authorColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAuthor()));
        yearColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getYear()));
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPrice()));
        categoryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));
        totalCopiesColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getTotalCopies()));
        availableCopiesColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getAvailableCopies()));

        idColumn.setPrefWidth(50);
        titleColumn.setPrefWidth(200);
        authorColumn.setPrefWidth(150);
        yearColumn.setPrefWidth(70);
        priceColumn.setPrefWidth(80);
        categoryColumn.setPrefWidth(130);
        totalCopiesColumn.setPrefWidth(70);
        availableCopiesColumn.setPrefWidth(90);

        bookTableView.getColumns().addAll(
                idColumn,
                titleColumn,
                authorColumn,
                yearColumn,
                priceColumn,
                categoryColumn,
                totalCopiesColumn,
                availableCopiesColumn
        );
    }

    private void configureCenter() {
        VBox tableWrapper = new VBox(bookTableView);
        VBox.setVgrow(bookTableView, Priority.ALWAYS);
        tableWrapper.setPadding(new Insets(10));
        root.setCenter(tableWrapper);
    }

    private void configureRightPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(350);
        panel.setStyle("-fx-background-color: #f5f5f5;");

        Label filterTitle = new Label("Поиск книг");
        filterTitle.setFont(Font.font(14));
        filterTitle.setStyle("-fx-font-weight: bold;");

        authorFilterTextField.setPromptText("Автор содержит...");
        yearFilterTextField.setPromptText("Год издания");
        categoryFilterTextField.setPromptText("Категория содержит...");

        VBox filterBox = new VBox(5,
                filterTitle,
                new Label("Автор:"),
                authorFilterTextField,
                new Label("Год:"),
                yearFilterTextField,
                new Label("Категория:"),
                categoryFilterTextField,
                new HBox(5, applyFilterButton, resetFilterButton)
        );

        Label addTitle = new Label("Добавление новой книги");
        addTitle.setFont(Font.font(14));
        addTitle.setStyle("-fx-font-weight: bold;");

        newTitleTextField.setPromptText("Название");
        newAuthorTextField.setPromptText("Автор");
        newYearTextField.setPromptText("Год");
        newPriceTextField.setPromptText("Цена");
        newCategoryTextField.setPromptText("Категория");
        newTotalCopiesTextField.setPromptText("Всего экземпляров");
        newAvailableCopiesTextField.setPromptText("В наличии");

        GridPane addGrid = new GridPane();
        addGrid.setHgap(5);
        addGrid.setVgap(5);

        int row = 0;
        addGrid.add(new Label("Название:"), 0, row);
        addGrid.add(newTitleTextField, 1, row++);
        addGrid.add(new Label("Автор:"), 0, row);
        addGrid.add(newAuthorTextField, 1, row++);
        addGrid.add(new Label("Год:"), 0, row);
        addGrid.add(newYearTextField, 1, row++);
        addGrid.add(new Label("Цена:"), 0, row);
        addGrid.add(newPriceTextField, 1, row++);
        addGrid.add(new Label("Категория:"), 0, row);
        addGrid.add(newCategoryTextField, 1, row++);
        addGrid.add(new Label("Всего:"), 0, row);
        addGrid.add(newTotalCopiesTextField, 1, row++);
        addGrid.add(new Label("В наличии:"), 0, row);
        addGrid.add(newAvailableCopiesTextField, 1, row++);
        addGrid.add(addBookButton, 1, row);

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(90);
        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);
        addGrid.getColumnConstraints().addAll(labelColumn, fieldColumn);

        Label priceTitle = new Label("Переоценка");
        priceTitle.setFont(Font.font(14));
        priceTitle.setStyle("-fx-font-weight: bold;");

        newPriceForSelectedTextField.setPromptText("Новая цена");
        HBox priceBox = new HBox(5, newPriceForSelectedTextField, repriceSelectedButton);
        priceBox.setAlignment(Pos.CENTER_LEFT);

        Label issueTitle = new Label("Выдача книги");
        issueTitle.setFont(Font.font(14));
        issueTitle.setStyle("-fx-font-weight: bold;");

        Label validateTitle = new Label("Валидация XML");
        validateTitle.setFont(Font.font(14));
        validateTitle.setStyle("-fx-font-weight: bold;");

        panel.getChildren().addAll(
                filterBox,
                new Separator(Orientation.HORIZONTAL),
                addTitle,
                addGrid,
                new Separator(Orientation.HORIZONTAL),
                priceTitle,
                priceBox,
                issueTitle,
                issueBookButton,
                validateTitle,
                validateXmlButton
        );

        root.setRight(panel);
    }

    private void configureBottom() {
        statusLabel.setPadding(new Insets(5, 10, 5, 10));
        root.setBottom(statusLabel);
    }

    public BorderPane getRoot() {
        return root;
    }

    public TableView<Book> getBookTableView() {
        return bookTableView;
    }

    public MenuItem getOpenXmlMenuItem() {
        return openXmlMenuItem;
    }

    public MenuItem getSaveMenuItem() {
        return saveMenuItem;
    }

    public MenuItem getSaveAsMenuItem() {
        return saveAsMenuItem;
    }

    public MenuItem getExitMenuItem() {
        return exitMenuItem;
    }

    public TextField getAuthorFilterTextField() {
        return authorFilterTextField;
    }

    public TextField getYearFilterTextField() {
        return yearFilterTextField;
    }

    public TextField getCategoryFilterTextField() {
        return categoryFilterTextField;
    }

    public Button getApplyFilterButton() {
        return applyFilterButton;
    }

    public Button getResetFilterButton() {
        return resetFilterButton;
    }

    public TextField getNewTitleTextField() {
        return newTitleTextField;
    }

    public TextField getNewAuthorTextField() {
        return newAuthorTextField;
    }

    public TextField getNewYearTextField() {
        return newYearTextField;
    }

    public TextField getNewPriceTextField() {
        return newPriceTextField;
    }

    public TextField getNewCategoryTextField() {
        return newCategoryTextField;
    }

    public TextField getNewTotalCopiesTextField() {
        return newTotalCopiesTextField;
    }

    public TextField getNewAvailableCopiesTextField() {
        return newAvailableCopiesTextField;
    }

    public Button getAddBookButton() {
        return addBookButton;
    }

    public TextField getNewPriceForSelectedTextField() {
        return newPriceForSelectedTextField;
    }

    public Button getRepriceSelectedButton() {
        return repriceSelectedButton;
    }

    public Button getIssueBookButton() {
        return issueBookButton;
    }

    public Button getValidateXmlButton() {
        return validateXmlButton;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }
}

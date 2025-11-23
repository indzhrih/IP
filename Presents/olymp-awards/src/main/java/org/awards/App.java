package org.awards;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.HBox;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class App extends Application {

    private final PrizeService service = new PrizeService(Locale.US);

    private ComboBox<Greeter> greeterCombo;
    private ComboBox<Recipient> recipientCombo;
    private ComboBox<Gift> giftCombo;
    private RadioButton rbConcertYes;
    private RadioButton rbConcertNo;
    private CheckBox cbLoyal;
    private Label totalLabel;
    private TextArea orderArea;

    @Override
    public void start(Stage stage) {
        GreeterRepository greeterRepo = GreeterRepository.fromResource("/data/greeters.txt", '|');
        RecipientRepository recipientRepo = RecipientRepository.fromResource("/data/recipients.txt", '|');

        greeterCombo = new ComboBox<>();
        greeterCombo.setPromptText("Выберите поздравителя");
        greeterCombo.getItems().setAll(greeterRepo.findAll());
        greeterCombo.valueProperty().addListener((obs, ov, nv) -> {
            giftCombo.getItems().clear();
            if (nv != null) {
                giftCombo.getItems().setAll(service.getGiftsForGreeter(nv.getName()));
                giftCombo.setDisable(giftCombo.getItems().isEmpty());
                if (!giftCombo.getItems().isEmpty()) giftCombo.getSelectionModel().selectFirst();
            } else {
                giftCombo.setDisable(true);
            }
            updateTotals();
        });

        recipientCombo = new ComboBox<>();
        recipientCombo.setPromptText("Кого поздравляем");
        recipientCombo.getItems().setAll(recipientRepo.findAll());
        recipientCombo.valueProperty().addListener((o, ov, nv) -> updateTotals());

        giftCombo = new ComboBox<>();
        giftCombo.setPromptText("Выберите подарок");
        giftCombo.setDisable(true);
        giftCombo.setMinWidth(320);
        giftCombo.setButtonCell(new ComboBoxListCell<>() {
            @Override
            public void updateItem(Gift g, boolean empty) {
                super.updateItem(g, empty);
                setText(empty || g == null ? "" : g.name() + " — " + service.money(g.price()));
            }
        });
        giftCombo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Gift g, boolean empty) {
                super.updateItem(g, empty);
                setText(empty || g == null ? "" : g.name() + " — " + service.money(g.price()));
            }
        });
        giftCombo.valueProperty().addListener((obs, ov, nv) -> updateTotals());

        rbConcertYes = new RadioButton("Да");
        rbConcertNo = new RadioButton("Нет");
        ToggleGroup tgConcert = new ToggleGroup();
        rbConcertYes.setToggleGroup(tgConcert);
        rbConcertNo.setToggleGroup(tgConcert);
        rbConcertNo.setSelected(true);
        rbConcertYes.selectedProperty().addListener((o, ov, nv) -> updateTotals());
        rbConcertNo.selectedProperty().addListener((o, ov, nv) -> updateTotals());

        cbLoyal = new CheckBox("Я постоянный клиент (скидка 10%)");
        cbLoyal.selectedProperty().addListener((o, ov, nv) -> updateTotals());

        totalLabel = new Label("Итого: " + service.money(0));
        totalLabel.getStyleClass().add("total-amount");

        orderArea = new TextArea();
        orderArea.setEditable(false);
        orderArea.setPrefRowCount(8);

        HBox greeterRow = new HBox(10, new Label("Поздравитель:"), greeterCombo);
        greeterRow.setAlignment(Pos.CENTER_LEFT);
        greeterRow.setPadding(new Insets(8));
        TitledPane greeterPane = new TitledPane("Поздравитель (из файла)", greeterRow);
        greeterPane.setCollapsible(false);

        HBox recRow = new HBox(10, new Label("Кого поздравляем:"), recipientCombo);
        recRow.setAlignment(Pos.CENTER_LEFT);
        recRow.setPadding(new Insets(8));
        TitledPane recipientPane = new TitledPane("Получатель (из файла)", recRow);
        recipientPane.setCollapsible(false);

        Label concertPriceHint = new Label("(фиксированно: " + service.money(PrizeService.CONCERT_COST) + ")");
        HBox concertRow = new HBox(12, new Label("Концерт:"), rbConcertYes, rbConcertNo, concertPriceHint);
        concertRow.setAlignment(Pos.CENTER_LEFT);
        concertRow.setPadding(new Insets(8));
        TitledPane concertPane = new TitledPane("Концерт", concertRow);
        concertPane.setCollapsible(false);

        HBox giftRow = new HBox(10, new Label("Подарок:"), giftCombo);
        giftRow.setAlignment(Pos.CENTER_LEFT);
        giftRow.setPadding(new Insets(8));
        TitledPane giftPane = new TitledPane("Подарок (зависит от поздравителя)", giftRow);
        giftPane.setCollapsible(false);

        VBox loyalBox = new VBox(8, cbLoyal);
        loyalBox.setPadding(new Insets(8));
        TitledPane loyalPane = new TitledPane("Скидка", loyalBox);
        loyalPane.setCollapsible(false);

        VBox resultBox = new VBox(8, totalLabel, new Label("Состав заказа:"), orderArea);
        resultBox.setPadding(new Insets(8));
        TitledPane resultPane = new TitledPane("Результат", resultBox);
        resultPane.setCollapsible(false);

        VBox root = new VBox(12, greeterPane, recipientPane, giftPane, concertPane, loyalPane, resultPane);
        root.setPadding(new Insets(16));
        root.setId("root");

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Scene scene = new Scene(scrollPane, 720, 640);
        scene.getStylesheets().add(App.class.getResource("/style/app.css").toExternalForm());
        stage.setTitle("Расчёт затрат на награждение победителей олимпиады");
        stage.setScene(scene);
        stage.show();
    }

    private void updateTotals() {
        Greeter greeter = greeterCombo.getValue();
        Recipient recipient = recipientCombo.getValue();
        Gift gift = giftCombo.isDisabled() ? null : giftCombo.getValue();
        boolean concert = rbConcertYes.isSelected();
        boolean loyal = cbLoyal.isSelected();

        int greeterFee = greeter != null ? greeter.getFee() : 0;
        int subtotal = service.calcSubtotal(gift, concert) + greeterFee;
        int total = service.applyLoyalDiscount(subtotal, loyal);

        List<String> lines = new ArrayList<>();
        lines.add("Поздравитель: " + (greeter != null ? greeter.getName() + " (" + service.money(greeterFee) + ")" : "—"));
        lines.add("Кого поздравляем: " + (recipient != null ? recipient.toString() : "—"));
        lines.add("Подарок: " + (gift != null ? gift.name() + " (" + service.money(gift.price()) + ")" : "—"));
        lines.add("Концерт: " + (concert ? "Да (" + service.money(PrizeService.CONCERT_COST) + ")" : "Нет"));
        if (loyal) lines.add("Скидка постоянного клиента: -10%");
        totalLabel.setText("Итого: " + service.money(total));
        lines.add("Итого к оплате: " + service.money(total));
        orderArea.setText(String.join("\n", lines));
    }

    public static void main(String[] args) {
        launch(args);
    }
}

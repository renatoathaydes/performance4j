package com.athaydes.performance4j;


import com.athaydes.performance4j.chart.DataSeries;
import com.athaydes.performance4j.ui.ChartTypeSelector;
import com.athaydes.performance4j.ui.SnapshotSupport;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static com.athaydes.performance4j.ui.RawDataInput.requestUserRawData;

public class App extends Application {

    private final BorderPane topBox = new BorderPane();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Performance4J");

        HBox buttonBox = new HBox(4);
        buttonBox.getStyleClass().add("buttons-box");
        buttonBox.setAlignment(Pos.BASELINE_LEFT);
        buttonBox.setPadding(new Insets(10));

        ObservableList<DataSeries> dataSeries = FXCollections.observableArrayList();

        Button addSeries = new Button("Add Series");
        addSeries.setOnAction(event -> requestUserRawData(stage, dataSeries));

        Button clearData = new Button("Clear");
        clearData.setOnAction(event -> dataSeries.clear());

        Button takeSnapshot = new Button("Save as picture");

        VBox chartBox = new VBox(4);

        ChartTypeSelector chartTypeSelector = new ChartTypeSelector(newChart -> {
            Node chart = newChart.getNodeWith("Data", dataSeries);
            takeSnapshot.setOnAction(event -> SnapshotSupport.takeSnapshot(stage, chart));
            chartBox.getChildren().setAll(chart);
        });

        buttonBox.getChildren().addAll(addSeries, clearData, chartTypeSelector, takeSnapshot);

        topBox.setTop(with(new BorderPane(buttonBox),
                b -> b.getStyleClass().add("main-buttons-pane")));
        topBox.setCenter(chartBox);
        topBox.getStyleClass().add("main-box");


        Scene scene = new Scene(topBox, 800, 600);

        String stylesheet = getParameters().getNamed().getOrDefault("stylesheet",
                "com/athaydes/performance4j/css/main.css");
        System.out.println("Using stylesheet " + stylesheet);
        scene.getStylesheets().add(stylesheet);

        if (stylesheet.startsWith("file:")) {
            // FIXME crude way to refresh stylesheet periodically
            scheduleRepeating(() -> {
                scene.getStylesheets().remove(stylesheet);
                scene.getStylesheets().add(stylesheet);
            });
        }

        stage.setScene(scene);
        stage.show();
    }

    private void scheduleRepeating(Runnable action) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("performance4j-repeating-action");
            return t;
        });
        executorService.scheduleAtFixedRate(() ->
                Platform.runLater(action), 3L, 3L, TimeUnit.SECONDS);
    }

    public static <T> T with(T thing, Consumer<T> takeThing) {
        takeThing.accept(thing);
        return thing;
    }

}

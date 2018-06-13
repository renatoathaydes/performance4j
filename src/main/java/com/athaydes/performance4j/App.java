package com.athaydes.performance4j;


import com.athaydes.performance4j.chart.DataSeries;
import com.athaydes.performance4j.ui.ChartTypeSelector;
import com.athaydes.performance4j.ui.SnapshotSupport;
import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static com.athaydes.performance4j.ui.RawDataInput.requestUserRawData;

public class App extends Application {

    public static final String DEFAULT_SPREADSHEET = "com/athaydes/performance4j/css/main.css";
    private final BorderPane topBox = new BorderPane();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Performance4J");
        Scene scene = new Scene(topBox, 800, 600);

        String stylesheetArg = getParameters().getNamed().getOrDefault("stylesheet", DEFAULT_SPREADSHEET);

        URI stylesheetURI = null;
        //noinspection StringEquality
        if (stylesheetArg != DEFAULT_SPREADSHEET) {
            stylesheetURI = new File(stylesheetArg).toURI();
        }

        final String stylesheet = stylesheetURI == null ? stylesheetArg : stylesheetURI.toString();
        System.out.println("Using stylesheet " + stylesheet);
        scene.getStylesheets().add(stylesheet);

        boolean watchStylesheet = getParameters().getUnnamed().contains("-w");

        if (watchStylesheet && stylesheetURI != null) {
            startWatchingStylesheet(scene, stylesheetArg, stylesheetURI, stylesheet);
        }

        HBox buttonBox = new HBox(4);
        buttonBox.getStyleClass().add("buttons-box");
        buttonBox.setAlignment(Pos.BASELINE_LEFT);
        buttonBox.setPadding(new Insets(10));

        ObservableList<DataSeries> dataSeries = FXCollections.observableArrayList();

        Button addSeries = new Button("Add Series");
        addSeries.setOnAction(event -> requestUserRawData(stage, dataSeries, stylesheet));

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

        stage.setScene(scene);
        stage.show();
    }

    private static void startWatchingStylesheet(Scene scene, String stylesheetArg,
                                                URI stylesheetURI, String stylesheet) {
        System.out.println("Watching stylesheet for changes");
        Thread watcher = new Thread(new StylesheetWatcher(Paths.get(stylesheetURI), () -> {
            if (new File(stylesheetArg).exists()) {
                Platform.runLater(() -> {
                    scene.getStylesheets().remove(stylesheet);
                    scene.getStylesheets().add(stylesheet);
                });
            } else {
                System.err.println("Stylesheet not found: " + stylesheetArg);
            }
        }));
        watcher.setDaemon(true);
        watcher.setName("stylesheet-watcher");
        watcher.start();
    }

    public static <T> T with(T thing, Consumer<T> takeThing) {
        takeThing.accept(thing);
        return thing;
    }

}

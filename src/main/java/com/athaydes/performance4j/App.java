package com.athaydes.performance4j;


import com.athaydes.performance4j.chart.DataSeries;
import com.athaydes.performance4j.ui.ChartTypeSelector;
import com.athaydes.performance4j.ui.SnapshotSupport;
import java.util.function.Consumer;
import javafx.application.Application;
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
        buttonBox.setAlignment(Pos.CENTER);
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

        topBox.setTop(new ScrollPane(buttonBox));
        topBox.setCenter(chartBox);
        topBox.getStyleClass().add("main-box");

        Scene scene = new Scene(topBox, 800, 600);
        scene.getStylesheets().add("com/athaydes/performance4j/css/main.css");

        stage.setScene(scene);
        stage.show();
    }

    public static <T> T with(T thing, Consumer<T> takeThing) {
        takeThing.accept(thing);
        return thing;
    }

}

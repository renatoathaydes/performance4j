package com.athaydes.performance4j;


import com.athaydes.performance4j.chart.P4JChart;
import com.athaydes.performance4j.chart.P4JLineChart;
import java.util.function.Consumer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

        P4JChart chart = new P4JLineChart();

        HBox buttonBox = new HBox(4);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        Button addSeries = new Button("Add Series");
        addSeries.setOnAction(event -> requestUserRawData(stage, chart));

        Button clearData = new Button("Clear");
        clearData.setOnAction(event -> chart.clear());

        buttonBox.getChildren().addAll(addSeries, clearData);

        VBox chartBox = new VBox(4);
        chartBox.getChildren().addAll(chart.getNode());

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

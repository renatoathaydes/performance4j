package com.athaydes.performance4j.ui;

import com.athaydes.performance4j.chart.DataSeries;
import java.io.StringReader;
import java.util.Scanner;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import static com.athaydes.performance4j.App.with;

public class RawDataInput {

    public static void requestUserRawData(Window owner, ObservableList<DataSeries> data,
                                          String stylesheet) {
        Stage dialog = new Stage(StageStyle.UNDECORATED);
        dialog.setTitle("Enter raw data");
        dialog.initOwner(owner);

        TextArea input;
        TextField name;
        Button cancel = new Button("Cancel");
        Button addSeries = new Button("Add series");

        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.getChildren().addAll(
                new Label("Series name:"),
                name = new TextField("Series 1"),
                new Label("Enter raw data:"),
                input = new TextArea("10, 20, 30, 20, 10"),
                with(new HBox(20, cancel, addSeries), b -> {
                    b.setAlignment(Pos.CENTER);
                    b.setPadding(new Insets(10));
                }));

        cancel.setOnAction(event -> dialog.hide());

        addSeries.setOnAction(event -> {
            dialog.hide();

            ProgressBar progressBar = new ProgressBar();
            Task<DataSeries> generateData = new ChartUpdater(name.getText(), input.getText());
            generateData.setOnSucceeded(e -> {
                DataSeries dataSeries = (DataSeries) e.getSource().getValue();
                data.add(dataSeries);
            });
            generateData.setOnFailed(e -> {
                // TODO show user why
                System.err.println("FAILED to update chart: " + e.getSource().getException());
            });
            progressBar.progressProperty().bind(generateData.progressProperty());
            new Thread(generateData).start();
            ProgressBarPopup.showPopup(owner, progressBar, generateData, stylesheet);
        });

        dialog.setScene(with(new Scene(box), s -> {
            s.getStylesheets().addAll(
                    stylesheet,
                    "com/athaydes/performance4j/css/data-input.css");
            box.getStyleClass().add("data-input");
        }));
        dialog.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ESCAPE == event.getCode()) {
                dialog.close();
            }
        });

        dialog.centerOnScreen();
        dialog.show();
    }

    private static class ChartUpdater extends Task<DataSeries> {

        private final String name;
        private final String text;

        ChartUpdater(String name, String text) {
            this.name = name;
            this.text = text;
            updateMessage("Parsing data...");
        }

        @Override
        protected DataSeries call() {
            Scanner scanner = new Scanner(new StringReader(text));
            scanner.useDelimiter("\\s*,\\s*");
            int size = (int) text.codePoints().filter(c -> c == ',').count() + 1;
            long[] data = new long[size];
            int i = 0;

            while (scanner.hasNext()) {
                data[i++] = scanner.nextLong();
                if (i % 1000 == 0) {
                    if (isCancelled()) {
                        break;
                    }
                    updateProgress(i, size);
                }
            }
            return new DataSeries(name, data);
        }
    }

}

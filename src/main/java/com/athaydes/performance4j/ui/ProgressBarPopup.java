package com.athaydes.performance4j.ui;

import com.athaydes.performance4j.AppState;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static com.athaydes.performance4j.App.with;

final class ProgressBarPopup {

    static void showPopup(AppState appState, ProgressBar progressBar, Task<?> task) {
        Stage dialog = new Stage(StageStyle.UNDECORATED);
        dialog.initOwner(appState.stage);

        Label label = new Label("Please wait");
        label.textProperty().bind(task.messageProperty());
        Button cancel = new Button("Cancel");

        cancel.setOnAction(event -> {
            task.cancel();
            dialog.hide();
        });

        task.runningProperty().addListener(observable -> {
            if (!task.isRunning()) {
                dialog.hide();
            }
        });

        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.getChildren().addAll(label, progressBar, cancel);

        dialog.setScene(with(new Scene(box), s -> s.getStylesheets().add(appState.stylesheet)));
        dialog.centerOnScreen();
        dialog.show();
    }
}

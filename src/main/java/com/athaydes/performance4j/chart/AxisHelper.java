package com.athaydes.performance4j.chart;

import com.athaydes.performance4j.AppState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static com.athaydes.performance4j.App.with;
import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

final class AxisHelper {

    static void makeRenamable(Axis<?> axis, AppState appState) {
        axis.addEventHandler(MOUSE_CLICKED, e -> {
            Stage dialog = new Stage(StageStyle.UNDECORATED);
            dialog.setTitle("Rename axis");
            dialog.initOwner(appState.stage);

            TextField name;
            Button cancel = new Button("Cancel");
            Button rename = new Button("Rename");

            VBox box = new VBox(10);
            box.setAlignment(Pos.CENTER);
            box.setPadding(new Insets(10));
            box.getChildren().addAll(
                    new Label("Series name:"),
                    name = new TextField(axis.getLabel()),
                    with(new HBox(20, cancel, rename), b -> {
                        b.setAlignment(Pos.CENTER);
                        b.setPadding(new Insets(10));
                    }));

            cancel.setOnAction(event -> dialog.hide());

            rename.setOnAction(event -> {
                dialog.hide();
                axis.setLabel(name.getText());
            });

            dialog.setScene(with(new Scene(box), s -> s.getStylesheets().add(appState.stylesheet)));
            dialog.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
                if (KeyCode.ESCAPE == event.getCode()) {
                    dialog.close();
                }
            });

            dialog.centerOnScreen();
            dialog.show();
        });

    }

}

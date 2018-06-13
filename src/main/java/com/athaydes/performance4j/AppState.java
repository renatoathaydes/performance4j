package com.athaydes.performance4j;

import javafx.stage.Stage;

public class AppState {
    public final Stage stage;
    public final String stylesheet;

    public AppState(Stage stage, String stylesheet) {
        this.stage = stage;
        this.stylesheet = stylesheet;
    }
}

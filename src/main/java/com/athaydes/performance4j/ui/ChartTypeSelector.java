package com.athaydes.performance4j.ui;

import com.athaydes.performance4j.chart.ChartType;
import com.athaydes.performance4j.chart.P4JChart;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;

public class ChartTypeSelector extends ChoiceBox<ChartType> {

    public ChartTypeSelector(Consumer<P4JChart> updateChart) {
        super(FXCollections.observableArrayList(ChartType.values()));
        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                updateChart.accept(newValue.getChart());
            }
        });
        ChartType initialChartType = ChartType.values()[0];
        getSelectionModel().select(initialChartType);
    }

}

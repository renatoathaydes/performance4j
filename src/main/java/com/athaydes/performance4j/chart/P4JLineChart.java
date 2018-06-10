package com.athaydes.performance4j.chart;

import com.athaydes.performance4j.transform.Transform;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class P4JLineChart implements P4JChart {

    private Transform transform = Transform.noOp();
    private final NumberAxis xAxis = new NumberAxis();
    private final NumberAxis yAxis = new NumberAxis();
    private final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

    private final Map<IntoData, XYChart.Series<Number, Number>> seriesByData = new HashMap<>();

    public P4JLineChart() {
        xAxis.setLabel("Run");
        yAxis.setLabel("Time");
    }

    public void setTitle(String title) {
        if (title == null) {
            title = "";
        }
        lineChart.setTitle(title);
    }

    public void setTransform(Transform transform) {
        if (transform == null) {
            transform = Transform.noOp();
        }
        this.transform = transform;
    }

    public void setAxisLabels(String xLabel, String yLabel) {
        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);
    }

    @Override
    public void add(IntoData data) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(data.seriesName());
        ObservableList<XYChart.Data<Number, Number>> seriesData = series.getData();
        for (XYChart.Data<Number, Number> datum : data) {
            seriesData.add(datum);
        }
        seriesByData.put(data, series);
        lineChart.getData().add(series);
    }

    @Override
    public void remove(IntoData data) {
        Optional.ofNullable(seriesByData.get(data)).ifPresent(s -> lineChart.getData().remove(s));
    }

    @Override
    public void clear() {
        lineChart.getData().clear();
    }

    @Override
    public Node getNode() {
        return lineChart;
    }

}

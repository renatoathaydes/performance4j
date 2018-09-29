package com.athaydes.performance4j.chart;

import com.athaydes.performance4j.AppState;
import com.athaydes.performance4j.transform.Smooth;
import com.athaydes.performance4j.transform.Transform;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class P4JLineChart implements P4JChart {

    private Transform transform = new Smooth(100);

    @Override
    public Node getNodeWith(String title, ObservableList<DataSeries> data, AppState appState) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Run");
        yAxis.setLabel("Time");
        if (appState != null) {
            AxisHelper.makeRenamable(xAxis, appState);
            AxisHelper.makeRenamable(yAxis, appState);
        }

        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(title);

        Runnable rebuildSeries = () -> {
            for (DataSeries datum : data) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                DataSeries dataSeries = transform.apply(datum);
                series.setName(dataSeries.getName());
                ObservableList<XYChart.Data<Number, Number>> seriesData = series.getData();
                int i = 0;
                for (long dataPoint : dataSeries.getData()) {
                    seriesData.add(new XYChart.Data<>(i, dataPoint));
                    i++;
                }
                lineChart.getData().add(series);
            }
        };

        rebuildSeries.run();

        if (appState != null) {
            data.addListener((InvalidationListener) observable -> {
                lineChart.getData().clear();
                rebuildSeries.run();
            });
        }

        return lineChart;
    }


}

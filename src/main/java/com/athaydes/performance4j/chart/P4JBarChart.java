package com.athaydes.performance4j.chart;

import java.util.LongSummaryStatistics;
import java.util.stream.LongStream;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class P4JBarChart implements P4JChart {

    @Override
    public Node getNodeWith(String title, ObservableList<DataSeries> data) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(title);

        Runnable rebuildSeries = () -> {
            for (DataSeries dataSeries : data) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(dataSeries.getName());
                LongSummaryStatistics summary = LongStream.of(dataSeries.getData()).summaryStatistics();
                ObservableList<XYChart.Data<String, Number>> seriesData = series.getData();
                seriesData.add(new XYChart.Data<>("Min", summary.getMin()));
                seriesData.add(new XYChart.Data<>("Avg", summary.getAverage()));
                seriesData.add(new XYChart.Data<>("Max", summary.getMax()));
                barChart.getData().add(series);
            }
        };

        rebuildSeries.run();
        data.addListener((InvalidationListener) observable -> {
            barChart.getData().clear();
            rebuildSeries.run();
        });

        return barChart;
    }

}

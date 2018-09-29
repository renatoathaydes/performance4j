package com.athaydes.performance4j.chart;

import com.athaydes.performance4j.AppState;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.List;
import java.util.LongSummaryStatistics;

import static java.util.Collections.singletonList;

public class P4JBarChart implements P4JChart {

    @Override
    public Node getNodeWith(String title, ObservableList<DataSeries> data, AppState appState) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Time");
        if (appState != null) {
            AxisHelper.makeRenamable(xAxis, appState);
            AxisHelper.makeRenamable(yAxis, appState);
        }

        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(title);

        Runnable rebuildSeries = () -> {
            for (DataSeries dataSeries : data) {
                populateBarChart(barChart, dataSeries.getSummary());
            }
        };

        rebuildSeries.run();

        if (appState != null) {
            data.addListener((InvalidationListener) observable -> {
                barChart.getData().clear();
                rebuildSeries.run();
            });
        }

        return barChart;
    }

    public Node getNodeWith(String title, DataSeriesSummary summary) {
        return getNodeWith(title, singletonList(summary));
    }

    public Node getNodeWith(String title, List<DataSeriesSummary> summaries) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Time");

        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(title);

        for (DataSeriesSummary summary : summaries) {
            populateBarChart(barChart, summary);
        }

        return barChart;
    }

    private void populateBarChart(BarChart<String, Number> barChart, DataSeriesSummary summary) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(summary.getName());
        ObservableList<XYChart.Data<String, Number>> seriesData = series.getData();
        LongSummaryStatistics stats = summary.getStats();
        seriesData.add(new XYChart.Data<>("Min", stats.getMin()));
        seriesData.add(new XYChart.Data<>("Avg", stats.getAverage()));
        seriesData.add(new XYChart.Data<>("Max", stats.getMax()));
        barChart.getData().add(series);
    }

}

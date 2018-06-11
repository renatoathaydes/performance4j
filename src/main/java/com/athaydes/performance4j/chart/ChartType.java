package com.athaydes.performance4j.chart;

public enum ChartType {

    LINE_CHART("Line Chart", new P4JLineChart()),
    BAR_CHART("Bar Chart", new P4JBarChart());

    private final String name;
    private P4JChart chart;

    ChartType(String name, P4JChart chart) {
        this.name = name;
        this.chart = chart;
    }

    public String getName() {
        return name;
    }

    public P4JChart getChart() {
        return chart;
    }

    @Override
    public String toString() {
        return name;
    }
}

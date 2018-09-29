package com.athaydes.performance4j.chart;

import java.util.stream.LongStream;

public final class DataSeries {

    private final String name;
    private final long[] data;

    public DataSeries(String name, long[] data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public long[] getData() {
        return data;
    }

    public DataSeriesSummary getSummary() {
        return new DataSeriesSummary(name, LongStream.of(data).summaryStatistics());
    }

}

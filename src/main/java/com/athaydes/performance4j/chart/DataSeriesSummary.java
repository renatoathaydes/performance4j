package com.athaydes.performance4j.chart;

import java.util.LongSummaryStatistics;

public final class DataSeriesSummary {
    private final String name;
    private final LongSummaryStatistics stats;

    public DataSeriesSummary(String name, LongSummaryStatistics stats) {
        this.name = name;
        this.stats = stats;
    }

    public String getName() {
        return name;
    }

    public LongSummaryStatistics getStats() {
        return stats;
    }
}

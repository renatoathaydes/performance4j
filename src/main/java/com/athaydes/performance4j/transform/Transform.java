package com.athaydes.performance4j.transform;

import com.athaydes.performance4j.chart.DataSeries;

@FunctionalInterface
public interface Transform {
    DataSeries apply(DataSeries data);

    static Transform noOp() {
        return (data) -> data;
    }
}

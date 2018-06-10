package com.athaydes.performance4j.transform;

import com.athaydes.performance4j.chart.IntoData;

@FunctionalInterface
public interface Transform {
    IntoData apply(IntoData data);

    static Transform noOp() {
        return (data) -> data;
    }
}

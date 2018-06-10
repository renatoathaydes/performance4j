package com.athaydes.performance4j.chart;

import javafx.scene.Node;

public interface P4JChart {
    void add(IntoData data);

    void remove(IntoData data);

    void clear();

    Node getNode();
}

package com.athaydes.performance4j.chart;

import javafx.collections.ObservableList;
import javafx.scene.Node;

public interface P4JChart {

    Node getNodeWith(String title, ObservableList<DataSeries> data);

}

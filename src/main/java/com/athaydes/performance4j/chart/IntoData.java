package com.athaydes.performance4j.chart;

import java.util.Iterator;
import java.util.NoSuchElementException;
import javafx.scene.chart.XYChart;

public interface IntoData extends Iterable<XYChart.Data<Number, Number>> {

    String seriesName();

    Object getData();

    class LongArrayIntoData implements IntoData {

        private final String seriesName;
        private final long[] data;

        public LongArrayIntoData(String seriesName, long[] data) {
            this.seriesName = seriesName;
            this.data = data;
        }

        @Override
        public long[] getData() {
            return data;
        }

        @Override
        public String seriesName() {
            return seriesName;
        }

        @Override
        public Iterator<XYChart.Data<Number, Number>> iterator() {
            return new Iterator<XYChart.Data<Number, Number>>() {
                int index;

                @Override
                public boolean hasNext() {
                    return index < data.length;
                }

                @Override
                public XYChart.Data<Number, Number> next() {
                    if (hasNext()) {
                        return new XYChart.Data<>(index, data[index++]);
                    }
                    throw new NoSuchElementException();
                }
            };
        }

    }

}

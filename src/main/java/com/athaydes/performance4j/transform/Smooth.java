package com.athaydes.performance4j.transform;

import com.athaydes.performance4j.chart.IntoData;
import java.util.stream.LongStream;

public class Smooth implements Transform {

    private final int maxPoints;
    private final SmoothFunction smoothFunction;

    public Smooth(int maxPoints) {
        this(maxPoints, new AverageSmoothFunction());
    }

    public Smooth(int maxPoints, SmoothFunction smoothFunction) {
        if (maxPoints < 2) {
            throw new IllegalArgumentException("maxPoints must be at least 2, but was " + maxPoints);
        }
        this.maxPoints = maxPoints;
        this.smoothFunction = smoothFunction;
    }

    @Override
    public IntoData apply(IntoData intoData) {
        long[] data;
        Object rawData = intoData.getData();
        if (rawData instanceof long[]) {
            data = (long[]) rawData;
        } else {
            return intoData;
        }
        if (data.length <= maxPoints) {
            return intoData;
        }
        int pointsPerPartition = data.length / maxPoints;
        int partitions = maxPoints;
        int lastPartitionSize = 0;
        if (data.length % maxPoints != 0) {
            pointsPerPartition++;
            partitions = data.length / pointsPerPartition;
            if (data.length % pointsPerPartition != 0) {
                partitions++;
            }
            lastPartitionSize = data.length - pointsPerPartition * (partitions - 1);
        }
        long[] result = new long[partitions];
        long[] partition = new long[pointsPerPartition];
        int resultIndex = 0;
        int partitionIndex = 0;
        for (int i = 0; i < data.length; i++) {
            partitionIndex = i % pointsPerPartition;
            partition[partitionIndex] = data[i];
            if (partitionIndex == pointsPerPartition - 1) {
                result[resultIndex] = smoothFunction.smoothen(partition);
                resultIndex++;
            }
        }
        if (resultIndex < result.length) {
            if (lastPartitionSize < pointsPerPartition) {
                long[] lastPartition = new long[lastPartitionSize];
                System.arraycopy(partition, 0, lastPartition, 0, lastPartitionSize);
                partition = lastPartition;
            }
            result[resultIndex] = smoothFunction.smoothen(partition);
        }

        return new IntoData.LongArrayIntoData(intoData.seriesName(), result);
    }

    public interface SmoothFunction {
        long smoothen(long[] data);
    }

    public static final class AverageSmoothFunction implements SmoothFunction {
        @Override
        public long smoothen(long[] data) {
            return Math.round(LongStream.of(data).average().orElse(0D));
        }
    }
}

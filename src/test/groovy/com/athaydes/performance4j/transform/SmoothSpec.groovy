package com.athaydes.performance4j.transform

import com.athaydes.performance4j.chart.DataSeries
import spock.lang.Specification
import spock.lang.Unroll

class SmoothSpec extends Specification {

    @Unroll
    def "Should be able to smoothen data points to fit into charts"() {
        when: 'the data is smoothened'
        def result = new Smooth(maxPoints).apply(new DataSeries('origin', toLongArray(data)))

        then: 'the result should be correctly smoothened data'
        result.data == toLongArray(smoothenedData)

        and: 'the name should have the (smooth) suffix'
        result.name == 'origin (smooth)'

        where:
        data                            | maxPoints | smoothenedData
        []                              | 10        | []
        [1L]                            | 10        | [1L]
        [0L]                            | 2         | [0L]
        [2L, 4L]                        | 2         | [2L, 4L]
        [2L, 4L, 6L]                    | 2         | [3L, 6L]
        [2L, 4L, 6L, 8L]                | 2         | [3L, 7L]
        [2L, 4L, 6L, 8L, 10L]           | 2         | [4L, 9L]
        [2L, 4L, 6L, 8L, 10L, 12L]      | 2         | [4L, 10L]
        [2L, 4L, 6L, 8L, 10L, 12L, 14L] | 2         | [5L, 12L]
        [2L, 4L, 6L, 8L, 10L, 12L, 14L] | 3         | [4L, 10L, 14L]
        [2L, 4L, 6L, 8L, 10L, 12L, 14L] | 4         | [3L, 7L, 11L, 14L]
        [2L, 4L, 6L, 8L, 10L, 12L, 14L] | 5         | [3L, 7L, 11L, 14L]
        [2L, 4L, 6L, 8L, 10L, 12L, 14L] | 6         | [3L, 7L, 11L, 14L]
        [2L, 4L, 6L, 8L, 10L, 12L, 14L] | 7         | [2L, 4L, 6L, 8L, 10L, 12L, 14L]
        [2L, 4L, 6L, 8L, 10L, 12L, 14L] | 8         | [2L, 4L, 6L, 8L, 10L, 12L, 14L]
        [0L]                            | 3         | [0L]
        [2L, 4L]                        | 3         | [2L, 4L]
        [2L, 4L, 6L]                    | 3         | [2L, 4L, 6L]
        [2L, 4L, 6L, 8L]                | 3         | [3L, 7L]
        [2L, 4L, 6L, 8L, 10L]           | 3         | [3L, 7L, 10L]
        [2L, 4L, 6L, 8L, 10L, 12L]      | 3         | [3L, 7L, 11L]
        [2L, 4L, 6L]                    | 4         | [2L, 4L, 6L]
        [2L, 4L, 6L, 8L]                | 4         | [2L, 4L, 6L, 8L]
        [2L, 4L, 6L, 8L, 10L]           | 4         | [3L, 7L, 10L]
        [2L, 4L, 6L, 8L, 10L, 12L]      | 4         | [3L, 7L, 11L]
        [2L, 4L, 6L, 8L, 10L, 12L, 14L] | 4         | [3L, 7L, 11L, 14L]
        [2L, 4L, 6L, 8L, 10L, 12L,
         14L, 16L]                      | 4         | [3L, 7L, 11L, 15L]
        [2L, 4L, 6L, 8L, 10L, 12L,
         14L, 16L, 18L]                 | 4         | [4L, 10L, 16L]
        [2L, 4L, 6L, 8L, 10L, 12L,
         14L, 16L, 18L, 20L]            | 4         | [4L, 10L, 16L, 20L]
        [2L, 4L, 6L, 8L, 10L, 12L,
         14L, 16L, 18L, 20L, 22L]       | 4         | [4L, 10L, 16L, 21L]
        [2L, 4L, 6L, 8L, 10L, 12L,
         14L, 16L, 18L, 20L, 22L, 24L]  | 4         | [4L, 10L, 16L, 22L]
        [2L, 4L, 6L, 8L, 10L, 12L, 14L,
         16L, 18L, 20L, 22L, 24L, 26L]  | 4         | [5L, 13L, 21L, 26L]
    }

    static long[] toLongArray(List items) {
        long[] result = new long[items.size()]
        for (int i = 0; i < items.size(); i++) {
            result[i] = (long) items.get(i)
        }
        result
    }

}

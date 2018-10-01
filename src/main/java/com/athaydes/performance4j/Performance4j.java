package com.athaydes.performance4j;

import com.athaydes.performance4j.chart.ChartType;
import com.athaydes.performance4j.chart.DataSeries;
import com.athaydes.performance4j.chart.DataSeriesSummary;
import com.athaydes.performance4j.chart.P4JBarChart;
import com.athaydes.performance4j.chart.Result;
import com.athaydes.performance4j.ui.SnapshotSupport;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.athaydes.performance4j.ui.SnapshotSupport.extensionOf;

public class Performance4j extends Application {

    private static final AtomicBoolean running = new AtomicBoolean(false);
    private static final CountDownLatch startLatch = new CountDownLatch(1);
    private static VBox sceneRoot;

    @Override
    public void start(Stage primaryStage) throws Exception {

        // not a real application - just used to start JavaFX stuff in headless mode
        Platform.setImplicitExit(false);
        sceneRoot = new VBox();
        primaryStage.setScene(new Scene(sceneRoot));

        startLatch.countDown();
    }

    public static void saveAsChart(LongSummaryStatistics summary, File location) {
        saveAsChart(new DataSeriesSummary("Series", summary), location);
    }

    public static void saveAsChart(DataSeriesSummary summary, File location) {
        saveTo(location, (callback) -> {
            Node chart = new P4JBarChart().getNodeWith("Data", summary);
            sceneRoot.getChildren().setAll(chart);
            SnapshotSupport.takeSnapshot(chart, location, callback);
        });
    }

    public static void saveAsChart(List<DataSeries> data, File location, ChartType chartType) {
        saveTo(location, (callback) -> {
            ObservableList<DataSeries> obsData = FXCollections.observableList(data);
            Node chart = chartType.getChart().getNodeWith("Data", obsData, null);
            sceneRoot.getChildren().setAll(chart);
            SnapshotSupport.takeSnapshot(chart, location, callback);
        });
    }

    private static void saveTo(File location, Consumer<Consumer<Result<Boolean>>> onFinished) {
        ensureJavaFXEngineStarted();
        ensureParentFileExists(location);

        boolean success = withJavaFXThread(onFinished);
        if (!success) {
            throw new RuntimeException("Unable to write image in the given format: " +
                    extensionOf(location.getName()));
        }
    }

    private static void ensureParentFileExists(File location) {
        Optional.ofNullable(location.getAbsoluteFile().getParentFile()).ifPresent(dir -> {
            boolean success = dir.isDirectory() || dir.mkdirs();
            if (!success) {
                throw new RuntimeException("Cannot create parent directory for chart file: " + dir);
            }
        });
    }

    private static <T> T withJavaFXThread(Consumer<Consumer<Result<T>>> onFinished) {
        BlockingDeque<Result<T>> resultDequeue = new LinkedBlockingDeque<>(1);

        Platform.runLater(() -> onFinished.accept(resultDequeue::offer));

        Result<T> result;
        try {
            result = resultDequeue.poll(15, TimeUnit.SECONDS);
            if (result == null) {
                throw new RuntimeException("Timeout while waiting for action to run");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return result.use((success) -> success, (error) -> {
            throw new RuntimeException(error);
        });
    }

    private static void ensureJavaFXEngineStarted() {
        if (!running.getAndSet(true)) {
            Thread t = new Thread(() -> Application.launch(Performance4j.class));
            t.setDaemon(true);
            t.start();
        }

        waitForStartLatch();
    }

    private static void waitForStartLatch() {
        boolean ok;
        try {
            ok = startLatch.await(500, TimeUnit.SECONDS);
            if (!ok) {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

package com.athaydes.performance4j;

import com.athaydes.performance4j.chart.ChartType;
import com.athaydes.performance4j.chart.DataSeries;
import com.athaydes.performance4j.ui.SnapshotSupport;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

    @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
    public static void saveAsChart(ChartType chartType, File location, List<DataSeries> data) {
        if (!running.getAndSet(true)) {
            Thread t = new Thread(() -> Application.launch(Performance4j.class));
            t.setDaemon(true);
            t.start();
        }

        waitFor(startLatch);

        Optional.ofNullable(location.getAbsoluteFile().getParentFile()).ifPresent(File::mkdirs);
        ObservableList<DataSeries> obsData = FXCollections.observableList(data);

        BlockingDeque resultDequeue = new LinkedBlockingDeque(1);

        Platform.runLater(() -> {
            try {
                Node chart = chartType.getChart().getNodeWith("Data", obsData);
                sceneRoot.getChildren().setAll(chart);
                SnapshotSupport.takeSnapshot(chart, location, resultDequeue);
            } catch (Exception e) {
                resultDequeue.offer(e);
            }
        });

        Object result;
        try {
            result = resultDequeue.poll(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (result instanceof Throwable) {
            throw new RuntimeException((Throwable) result);
        }
    }

    private static void waitFor(CountDownLatch latch) {
        boolean ok;
        try {
            ok = latch.await(500, TimeUnit.SECONDS);
            if (!ok) {
                throw new TimeoutException();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

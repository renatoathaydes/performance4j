package com.athaydes.performance4j.ui;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Queue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javax.imageio.ImageIO;

public final class SnapshotSupport {

    private static final FileChooser.ExtensionFilter filter =
            new FileChooser.ExtensionFilter("PNG image", "png");

    public static void takeSnapshot(Window owner, Node node) {
        FileChooser chooser = new FileChooser();
        chooser.setSelectedExtensionFilter(filter);
        chooser.setInitialFileName("chart.png");
        chooser.setTitle("Select a location to save the file");
        File file = chooser.showSaveDialog(owner);
        if (file != null) {
            Queue snapshotResult = new ArrayDeque(1);
            takeSnapshot(node, file, snapshotResult);
            // TODO check result
        }
    }

    @SuppressWarnings("unchecked")
    public static void takeSnapshot(Node node, File file, Queue resultDequeue) {
        String extension = extensionOf(file.getName());
        node.snapshot(snapshot -> {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot.getImage(), null), extension, file);
            } catch (Exception e) {
                resultDequeue.offer(e);
            } finally {
                if (resultDequeue != null) {
                    resultDequeue.offer(true);
                }
            }
            return null;
        }, null, null);
    }

    private static String extensionOf(String name) {
        int index = name.lastIndexOf('.');
        if (index < 0 || index == name.length() - 1) {
            return "png";
        }
        String ext = name.substring(index + 1);
        if (!filter.getExtensions().contains(ext)) {
            return "png";
        }
        return ext;
    }

}

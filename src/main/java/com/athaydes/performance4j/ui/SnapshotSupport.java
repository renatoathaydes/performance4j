package com.athaydes.performance4j.ui;

import java.awt.image.BufferedImage;
import java.io.File;
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
            String extension = extensionOf(file.getName());
            node.snapshot(snapshot -> {
                try {
                    BufferedImage rendered = SwingFXUtils.fromFXImage(snapshot.getImage(), null);
                    if (rendered == null) {
                        System.err.println("Image could not be created");
                    } else {
                        ImageIO.write(rendered, extension, file);
                    }
                } catch (Exception e) {
                    // TODO show error to the user
                    e.printStackTrace();
                }
                return null;
            }, null, null);
        }
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

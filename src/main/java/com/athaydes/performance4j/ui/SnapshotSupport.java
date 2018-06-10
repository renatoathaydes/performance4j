package com.athaydes.performance4j.ui;

import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import javax.imageio.ImageIO;

public final class SnapshotSupport {

    static void takeSnapshot(Window owner, Node node) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select a directory");
        File dir = chooser.showDialog(owner);
        if (dir != null) {
            File file = new File(dir, "results-line-chart.png");
            node.snapshot(snapshot -> {
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(snapshot.getImage(), null), "png", file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }, null, null);
        }
    }

}

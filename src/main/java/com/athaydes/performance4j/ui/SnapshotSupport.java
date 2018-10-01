package com.athaydes.performance4j.ui;

import com.athaydes.performance4j.chart.Result;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.function.Consumer;

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
            takeSnapshot(node, file, (result) -> {
                // TODO show error in UI
                result.use(sucess -> {
                    if (!sucess) {
                        System.err.println("Format unsupported, could not save image");
                    }
                }, Throwable::printStackTrace);
            });
        }
    }

    @SuppressWarnings("unchecked")
    public static void takeSnapshot(Node node, File file, Consumer<Result<Boolean>> resultAction) {
        String extension = extensionOf(file.getName());
        node.snapshot(snapshot -> {
            try {
                boolean success = ImageIO.write(SwingFXUtils.fromFXImage(snapshot.getImage(), null), extension, file);
                resultAction.accept(new Result<>(success));
            } catch (Exception e) {
                resultAction.accept(new Result<>(e));
            }
            return null;
        }, null, null);
    }

    public static String extensionOf(String name) {
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

package com.athaydes.performance4j;

import com.sun.nio.file.SensitivityWatchEventModifier;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

final class StylesheetWatcher implements Runnable {

    private final Path path;
    private final Runnable onChange;

    StylesheetWatcher(Path path, Runnable onChange) {
        this.path = path;
        this.onChange = onChange;
    }

    @Override
    public void run() {
        WatchKey watchKey = null;
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            watchKey = path.getParent().register(watchService, new WatchEvent.Kind[]{
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.OVERFLOW,
                    StandardWatchEventKinds.ENTRY_DELETE
            }, SensitivityWatchEventModifier.HIGH);


            while (true) {
                WatchKey wk = watchService.take();
                for (WatchEvent<?> event : wk.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    Path changed = (Path) event.context();
                    if (path.getFileName().equals(changed.getFileName())) {
                        onChange.run();
                    }
                }
                // reset the key
                boolean valid = wk.reset();
                if (!valid) {
                    System.err.println("Invalid watcher, cannot continue watching " + path);
                    break;
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            System.err.println("Stylesheet watcher has stopped");
        } finally {
            if (watchKey != null) {
                watchKey.cancel();
            }
        }
    }

}

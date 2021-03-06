package common;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import client.Client;
import client.Snapshot;

public class FileWatcher extends Thread {
    private final File file;
    private Client client;
    private AtomicBoolean stop = new AtomicBoolean(false);

    public FileWatcher(File file, Client client) {
    	this.client = client;
        this.file = file;
    	System.out.println("file watcher constructor");
    	System.out.println("Listening for changes in: " + file.toPath());
    }

    public boolean isStopped() { return stop.get(); }
    public void stopThread() { stop.set(true); }

    public void doOnChange() throws JSchException, IOException, SftpException {
        // Write the snapshot to NVM
    	System.out.println("file changed !");
    	Snapshot snaphotNVM = client.getSnapshot();
    	snaphotNVM.takeSnapShot();
    	
    }
    
    // boilerplate code to listen to changes to a file
    @Override
    public void run() {
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            Path path = file.toPath().getParent();
//            System.out.println(file.toPath());
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            while (!isStopped()) {
                WatchKey key;
                try { key = watcher.poll(25, TimeUnit.MILLISECONDS); }
                catch (InterruptedException e) { return; }
                if (key == null) { Thread.yield(); continue; }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        Thread.yield();
                        continue;
                    } else if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
                            && filename.toString().equals(file.getName())) {
                        doOnChange();
                    }
                    boolean valid = key.reset();
                    if (!valid) { break; }
                }
                Thread.yield();
            }
        } catch (Throwable e) {
            // Log or rethrow the error
        	e.printStackTrace();
        }
    }
}

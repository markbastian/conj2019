package conj2019.horsemen.app.v0;

import com.squareup.tape.QueueFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.Date;

@Component
public class NewDataFileSystemWatcher {
    private final Path filePath = Paths.get("example/");

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private WatchService watchService;

    @Autowired
    private QueueFile queue;

    @Autowired
    FileEntryRepository repository;

    private void watch() {
        while (true) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException ex) {
                return;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                Path path = (Path) event.context();
                Path dir = (Path)key.watchable();
                Path fullPath = dir.resolve(path);

                System.out.println("Enqueueing file " +
                        path.getFileName() +
                        " with event " +
                        kind.name() +
                        ".");

                System.out.println(fullPath);
                System.out.println("Exists? " + Files.exists(fullPath));

                try (BufferedReader br = Files.newBufferedReader(fullPath)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        queue.add(line.getBytes());
                    }
                    System.out.println("Enqueue file " + fullPath.getFileName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                FileEntry fileEntry = new FileEntry(path.getFileName().toString(), new Date());
                repository.save(fileEntry);
            }
            key.reset();
        }
    }

    public NewDataFileSystemWatcher() {
    }

    @PostConstruct
    public void init() throws IOException {
        filePath.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                watch();
            }
        });
    }
}

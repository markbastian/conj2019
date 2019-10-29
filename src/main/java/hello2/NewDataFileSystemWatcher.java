package hello2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;

@Component
public class NewDataFileSystemWatcher {
    private final Path filePath = Paths.get("example/");

    @Autowired
    private TaskExecutor taskExecutor;

    //@Autowired
    private WatchService watchService = FileSystems.getDefault().newWatchService();

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

                System.out.println("kind " + kind.name());
                Path path = (Path) event.context();
                System.out.println(path.toString());

//                if(kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
//                    readOffer(path.toString());
//                }
            }
            key.reset();
        }
    }

    public NewDataFileSystemWatcher() throws IOException {
    }

    @PostConstruct
    public void init() throws IOException {
        filePath.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                watch();
            }
        });
    }
}

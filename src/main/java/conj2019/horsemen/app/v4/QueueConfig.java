package conj2019.horsemen.app.v4;

import com.squareup.tape.QueueFile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


@Configuration
public class QueueConfig {
    @Bean
    public QueueFile getQueueFile() throws IOException {
        Path filePath = Paths.get("queue_tmp/");
        return new QueueFile(filePath.toFile());
    }
}

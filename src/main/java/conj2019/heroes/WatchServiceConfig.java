package conj2019.heroes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;

@Configuration
public class WatchServiceConfig {
    @Bean
    public WatchService getWatchService() throws IOException {
        return FileSystems.getDefault().newWatchService();
    }
}

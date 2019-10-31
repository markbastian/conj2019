package conj2019.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        final Properties properties = new Properties();
        properties.put("server.port", "8083");
        app.setDefaultProperties(properties);
        app.run(args);
    }
}
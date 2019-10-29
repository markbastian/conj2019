package conj2019.heroes;

import com.squareup.tape.QueueFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

@Component
public class ScheduledTasks {
    @Autowired
    SuperPersonRepository repository;

    @Autowired
    private QueueFile queue;

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 1000)
    public void reportCurrentTime() throws IOException {
        byte[] data = queue.peek();

        if (null != data){
            queue.remove();
            String line = new String(data);
            String[] frags = line.split(",");
            for(int i = 0; i < frags.length; i++){
                frags[i] = frags[i].trim();
            }
            String name = frags[0];
            String universe = frags[1];
            String[] powers = Arrays.copyOfRange(frags, 2, frags.length);
            SuperPerson superPerson = new SuperPerson(name, universe, powers);
            log.info("Persisting super : {}.", superPerson);
            repository.save(superPerson);
            log.info("Super persisted!");
        }
    }
}
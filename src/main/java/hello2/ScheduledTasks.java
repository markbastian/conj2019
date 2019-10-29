package hello2;

import com.squareup.tape.QueueFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;

//Note that this is coupled completely.
//the partsbin example is totally decoupled
@Component
public class ScheduledTasks {
    @Autowired
    SuperPersonRepository repository;

    @Autowired
    private QueueFile queue;

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() throws IOException {
        byte[] data = queue.peek();

        if (null != data){
            queue.remove();
            String line = new String(data);
            String[] frags = line.split(",");
            String first = frags[0].trim();
            String last = frags[1].trim();
            SuperPerson superPerson = new SuperPerson(first, last);
            log.info("Persisting customer : {}.", superPerson);
            repository.save(superPerson);
            log.info("Customer persisted!");
        }
    }
}
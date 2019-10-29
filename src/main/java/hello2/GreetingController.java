package hello2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {
    @Autowired
    FileEntryRepository fileEntryRepository;

    @Autowired
    CustomerRepository customerRepository;

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

//    500 :(
    @RequestMapping("/files_v0")
    public Iterable<FileEntry> files_v0() {
        return fileEntryRepository.findAll();
    }

    @RequestMapping("/files_v1")
    public String files_v1() {
        //Problem? - toString on FileEntry or something else?
        //[hello2.FileEntry@7817c48c]
        return fileEntryRepository.findAll().toString();
    }

    @RequestMapping("/files")
    public Collection<FileEntry> files() {
        ArrayList<FileEntry> entries = new ArrayList<>();
        for(FileEntry entry : fileEntryRepository.findAll()){
            entries.add(entry);
        }
        return entries;
    }
}
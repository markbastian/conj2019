package conj2019.supers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;

@RestController
public class WebApiController {
    @Autowired
    FileEntryRepository fileEntryRepository;

    @Autowired
    SuperPersonRepository superPersonRepository;

    @RequestMapping("/files")
    public Collection<FileEntry> files() {
        ArrayList<FileEntry> entries = new ArrayList<>();
        for(FileEntry entry : fileEntryRepository.findAll()){
            entries.add(entry);
        }
        return entries;
    }

    @RequestMapping("/supers")
    public Collection<SuperPerson> supers() {
        ArrayList<SuperPerson> entries = new ArrayList<>();
        for(SuperPerson entry : superPersonRepository.findAll()){
            entries.add(entry);
        }
        return entries;
    }
}
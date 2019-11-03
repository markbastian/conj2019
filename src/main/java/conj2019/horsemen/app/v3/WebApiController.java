package conj2019.horsemen.app.v3;

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
    HorsemanRepository horsemanRepository;

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

    @RequestMapping("/horsemen")
    public Collection<Horseman> horsemen() {
        ArrayList<Horseman> entries = new ArrayList<>();
        for(Horseman entry : horsemanRepository.findAll()){
            entries.add(entry);
        }
        return entries;
    }
}
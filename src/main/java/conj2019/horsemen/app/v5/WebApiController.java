package conj2019.horsemen.app.v5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WebApiController {
    @Autowired
    FileEntryRepository fileEntryRepository;

    @Autowired
    HorsemanRepository horsemanRepository;

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

    @RequestMapping("/weapons")
    public Map<String, String[]> weapons(@RequestParam(value="name") String name) {
        Map<String, String[]> res = new HashMap<>();
        for(Horseman horseman : horsemanRepository.findByName(name)){
            res.put(name, horseman.getWeapons());
            return res;
        }
        return res;
    }
}
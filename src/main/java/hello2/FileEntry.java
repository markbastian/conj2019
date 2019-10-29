package hello2;

import javax.persistence.Entity;
import java.sql.Date;

//@Entity
public class FileEntry {
    private String fileName;
    private Date insertionDate;

    public FileEntry(String filename, Date insertionDate){
        this.fileName = filename;
        this.insertionDate = insertionDate;
    }
}

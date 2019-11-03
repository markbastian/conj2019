package conj2019.horsemen.app.v0;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class FileEntry {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String fileName;
    private Date insertionDate;

    protected FileEntry() {}

    public FileEntry(String filename, Date insertionDate){
        this.fileName = filename;
        this.insertionDate = insertionDate;
    }

    public String getFileName() {
        return fileName;
    }

    public Date getInsertionDate() {
        return insertionDate;
    }

    @Override
    public String toString() {
        return String.format(
                "FileEntry[id=%d, fileName='%s', insertionDate='%s']",
                id, fileName, insertionDate);
    }
}

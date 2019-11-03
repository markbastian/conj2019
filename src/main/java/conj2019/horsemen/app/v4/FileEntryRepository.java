package conj2019.horsemen.app.v4;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FileEntryRepository extends CrudRepository<FileEntry, Long> {

    List<FileEntry> findByFileName(String fileName);

    FileEntry findById(long id);
}
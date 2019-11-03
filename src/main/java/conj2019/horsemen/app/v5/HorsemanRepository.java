package conj2019.horsemen.app.v5;


import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HorsemanRepository extends CrudRepository<Horseman, Long> {

    List<Horseman> findByName(String name);

    Horseman findById(long id);
}
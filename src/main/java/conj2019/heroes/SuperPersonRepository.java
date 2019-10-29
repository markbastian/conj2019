package conj2019.heroes;


import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface SuperPersonRepository extends CrudRepository<SuperPerson, Long> {

    List<SuperPerson> findByName(String name);

    SuperPerson findById(long id);
}
package hello2;


import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface SuperPersonRepository extends CrudRepository<SuperPerson, Long> {

    List<SuperPerson> findByLastName(String lastName);

    SuperPerson findById(long id);
}
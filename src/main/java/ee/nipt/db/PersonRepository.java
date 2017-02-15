package ee.nipt.db;

import ee.nipt.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, String> {
    Optional<Person> findOneByEmail(String email);

    List<Person> findManyByCreator(String creator);
}

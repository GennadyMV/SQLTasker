package wepaht.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import wepaht.domain.Database;

import java.util.List;

@RestResource(exported = false)
public interface DatabaseRepository extends JpaRepository<Database, Long>{

    public List<Database> findByName(String name);
}

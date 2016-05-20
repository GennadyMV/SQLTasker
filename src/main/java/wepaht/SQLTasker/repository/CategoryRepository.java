package wepaht.SQLTasker.repository;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import wepaht.SQLTasker.domain.Category;
import java.util.List;

@RestResource(exported = false)
public interface CategoryRepository extends JpaRepository<Category, Long>{

    List<Category> findByStartsBefore(LocalDate date);

    List<Category> findByStarts(LocalDate date);

    List<Category> findByStartsBeforeAndExpiresAfter(LocalDate date1, LocalDate date2);
}

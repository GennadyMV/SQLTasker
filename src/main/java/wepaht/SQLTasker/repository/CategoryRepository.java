package wepaht.SQLTasker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import wepaht.SQLTasker.domain.Category;


import java.util.Date;
import java.util.List;

@RestResource(exported = false)
public interface CategoryRepository extends JpaRepository<Category, Long>{

    List<Category> findByStartDateBefore(Date date);

    List<Category> findByStartDate(Date date);

}

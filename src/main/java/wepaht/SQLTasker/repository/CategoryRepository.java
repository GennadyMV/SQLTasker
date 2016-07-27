package wepaht.SQLTasker.repository;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import wepaht.SQLTasker.domain.Category;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.TmcAccount;

@RestResource(exported = false)
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Override
    @Query("SELECT c FROM Category c WHERE c.id=:id AND c.deleted=false")
    Category findOne(@Param("id") Long id);
    
    @Override
    @Query("SELECT c FROM Category c WHERE c.deleted=false")
    List<Category> findAll();
    
    List<Category> findByOwnerAndDeletedFalse(TmcAccount owner);
    
    @Query("SELECT COUNT(task) as taskCount FROM Category cat "
            + "JOIN cat.taskList task "
            + "WHERE cat IN :cats "
            + "AND task.deleted=false")
    Long getTaskCountByCategories(@Param("cats") List<Category> categories);
    
    @Query("SELECT COUNT(task) as taskCount FROM Category c "
            + "JOIN c.taskList task "
            + "WHERE c=:cat")
    Long getTaskCountByCategory(@Param("cat") Category category);
}

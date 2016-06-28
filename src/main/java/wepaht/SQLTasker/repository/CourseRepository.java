package wepaht.SQLTasker.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wepaht.SQLTasker.domain.LocalAccount;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.TmcAccount;

public interface CourseRepository extends JpaRepository<Course, Long>{
    
    @Override
    @Query("SELECT c FROM Course c WHERE c.id=:id AND c.deleted=false")
    Course findOne(@Param("id") Long id);
    
    @Override
    @Query("SELECT c FROM Course c WHERE c.deleted=false")
    List<Course> findAll();
    
    @Query("SELECT c.courseCategories FROM Course c WHERE c = :course AND c.deleted=false")
    List<Category> getCourseCategories(@Param("course") Course course);
    
    List<Course> findByNameAndDeletedFalse(String name);
    
    @Query("SELECT c.students FROM Course c WHERE c = :course AND c.deleted=false")
    List<TmcAccount> getCourseStudents(@Param("course") Course course);
    
    @Query("SELECT c FROM Course c WHERE (starts <= :now OR starts IS NULL) AND (expires >= :now OR expires IS NULL) AND c.deleted=false")
    List<Course> findByStartsBeforeAndExpiresAfter(@Param("now") LocalDate now);    
}

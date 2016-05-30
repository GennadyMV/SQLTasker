package wepaht.SQLTasker.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Course;

public interface CourseRepository extends JpaRepository<Course, Long>{
    
    @Query("SELECT c.courseCategories FROM Course c WHERE c = :course")
    List<Category> getCourseCategories(@Param("course") Course course);
    
    List<Course> findByName(String name);
}

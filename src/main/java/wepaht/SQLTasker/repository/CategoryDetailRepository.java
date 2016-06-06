package wepaht.SQLTasker.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.CategoryDetail;
import wepaht.SQLTasker.domain.Course;

public interface CategoryDetailRepository extends JpaRepository<CategoryDetail, Long>{
    
    List<CategoryDetail> findByCourseOrderByStartsAscExpiresDesc(Course course);
    
    List<CategoryDetail> findByCourseAndCategory(Course course, Category category);
    
    @Query("DELETE FROM CategoryDetail WHERE course=:course")
    void deleteCategoryDetailsByCourse(@Param("course") Course course);
    
    @Query("DELETE FROM CategoryDetail WHERE category=:category")
    void deleteCategoryDetailsByCategory(@Param("category") Category category);
}

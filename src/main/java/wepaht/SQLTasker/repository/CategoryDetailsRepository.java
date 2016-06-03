package wepaht.SQLTasker.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.CategoryDetail;
import wepaht.SQLTasker.domain.Course;

public interface CategoryDetailsRepository extends JpaRepository<CategoryDetail, Long>{
    
    List<CategoryDetail> findByCourse(Course course);
    
    List<CategoryDetail> findByCourseAndCategory(Course course, Category category);
}

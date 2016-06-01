package wepaht.SQLTasker.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import wepaht.SQLTasker.domain.CategoryDetails;
import wepaht.SQLTasker.domain.Course;

public interface CategoryDetailsRepository extends JpaRepository<CategoryDetails, Long>{
    
    List<CategoryDetails> findByCourse(Course course);
}

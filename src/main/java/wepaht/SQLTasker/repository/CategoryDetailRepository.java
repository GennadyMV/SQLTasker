package wepaht.SQLTasker.repository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
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
    
    @Query("SELECT d FROM CategoryDetail d WHERE (d.starts <= :now OR d.starts IS NULL) AND (d.expires >= :now OR d.expires IS NULL) AND d.course = :course")
    List<CategoryDetail> findActiveDetailsByCourse(@Param("now") LocalDate now, @Param("course") Course course);
    
    @Query("SELECT d FROM CategoryDetail d "
            + "WHERE (starts IS NULL OR starts <= :now) "
            + "AND (expires IS NULL OR expires >= :now) "
            + "AND course = :course "
            + "AND category = :category")
    CategoryDetail findActiveDetailByCourseAndCategory(@Param("now") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate now, @Param("course") Course course, @Param("category") Category category);
}

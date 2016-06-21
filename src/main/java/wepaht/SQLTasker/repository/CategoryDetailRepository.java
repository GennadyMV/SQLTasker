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
    
    @Override
    @Query("SELECT d FROM CategoryDetail d WHERE d.course.deleted=false AND d.category.deleted=false")
    List<CategoryDetail> findAll();
    
    @Override
    @Query("SELECT d FROM CategoryDetail d WHERE d.id=:id AND d.category.deleted=false AND d.course.deleted=false")
    CategoryDetail findOne(@Param("id") Long id);
    
    @Query("SELECT d FROM CategoryDetail d Where d.course=:course AND d.course.deleted=false AND d.category.deleted=false")
    List<CategoryDetail> findByCourseOrderByStartsAscExpiresDesc(@Param("course") Course course);
    
    @Query("SELECT d FROM CategoryDetail d WHERE d.course=:course AND d.category=:category AND d.category.deleted=false AND d.course.deleted=false")
    List<CategoryDetail> findByCourseAndCategory(@Param("course") Course course, @Param("category") Category category);
    
    
//    @Query("SELECT d FROM CategoryDetail d WHERE (d.starts <= :now OR d.starts IS NULL) AND (d.expires >= :now OR d.expires IS NULL) AND d.course = :course")
//    List<CategoryDetail> findActiveDetailsByCourse(@Param("now") LocalDate now, @Param("course") Course course);
//    
//    @Query("SELECT d FROM CategoryDetail d "
//            + "WHERE (starts IS NULL OR starts <= :now) "
//            + "AND (expires IS NULL OR expires >= :now) "
//            + "AND course = :course "
//            + "AND category = :category")
//    CategoryDetail findActiveDetailByCourseAndCategory(@Param("now") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate now, @Param("course") Course course, @Param("category") Category category);
}

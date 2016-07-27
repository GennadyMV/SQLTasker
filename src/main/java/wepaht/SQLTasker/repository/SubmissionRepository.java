package wepaht.SQLTasker.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wepaht.SQLTasker.domain.LocalAccount;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.PointHolder;
import wepaht.SQLTasker.domain.Submission;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("SELECT s.task.name, s.points FROM Submission s WHERE :account=account")
    List<?> getTaskNameAndPointsByAccount(@Param("account") TmcAccount account);

    @Query("SELECT COUNT(*) FROM Submission WHERE :account=account AND points=true")
    Integer getPointsByAccount(@Param("account") TmcAccount account);

    @Query("SELECT account.username, COUNT(*) AS points FROM Submission WHERE points=true GROUP BY account")
    List<?> getAllPoints();
    
    @Query("SELECT new wepaht.SQLTasker.domain.PointHolder(s.account.username, COUNT(s)) FROM Submission s WHERE points=true GROUP BY s.account")
    List<PointHolder> exportAllPoints();
    
    @Query("SELECT COUNT(*) FROM Submission WHERE :account=account AND :course=course AND points=true")
    Integer getPointsByAccountAndCourse(@Param("account") TmcAccount account, @Param("course") Course course);

    @Query("SELECT COUNT(*) FROM Submission WHERE :account=account AND :course=course AND :category=category AND points=true")
    public Integer getPointsByAccountAndCourseAndCategory(@Param("account")TmcAccount account, @Param("course") Course course, @Param("category") Category category);

    public List<Submission> findByAccountAndCourseAndCategoryAndTaskAndPoints(TmcAccount account, Course course, Category category, Task task, Boolean points);
    
    @Query("SELECT COUNT(DISTINCT sub.task) FROM Submission sub "
            + "WHERE sub.account=:account "
            + "AND sub.course=:course "
            + "AND sub.category=:cat "
            + "AND sub.task.deleted = false "            
            + "AND sub.points=true")
    Long countPointsByAccountAndCourseAndCategory(@Param("account") TmcAccount account, @Param("course") Course course, @Param("cat") Category category);
    
    @Query("SELECT COUNT(sub.task) FROM Submission sub "
            + "WHERE sub.account=:account "
            + "AND sub.course=:course "
            + "AND sub.points=true "
            + "AND sub.task.deleted=false "
            + "AND sub.category.deleted=false "
            + "GROUP By sub.task, sub.category")
    Long countPointsByAccountAndCourse(@Param("account") TmcAccount account, @Param("course") Course course);
}

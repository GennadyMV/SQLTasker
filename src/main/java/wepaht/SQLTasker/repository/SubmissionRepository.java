package wepaht.SQLTasker.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.PointHolder;
import wepaht.SQLTasker.domain.Submission;
import wepaht.SQLTasker.domain.Task;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("SELECT s.task.name, s.points FROM Submission s WHERE :account=account")
    List<?> getTaskNameAndPointsByAccount(@Param("account") Account account);

    @Query("SELECT COUNT(*) FROM Submission WHERE :account=account AND points=true")
    Integer getPointsByAccount(@Param("account") Account account);

//    @Query("SELECT new wepaht.SQLTasker.domain.PointHolder(q.username, COUNT(q)) FROM PastQuery q WHERE q.awarded=true AND q.correct=true GROUP BY q.username")
//    List<PointHolder> exportAllPoints();    
    
//    @Query("SELECT username, COUNT(*) AS points FROM PastQuery WHERE awarded=true AND correct=true GROUP BY username")
//    List<?> getPoints();
    @Query("SELECT account.username, COUNT(*) AS points FROM Submission WHERE points=true GROUP BY Account")
    List<?> getAllPoints();
    
    @Query("SELECT new wepaht.SQLTasker.domain.PointHolder(s.account.username, COUNT(s)) FROM Submission s WHERE points=true GROUP BY s.account")
    List<PointHolder> exportAllPoints();
    
    @Query("SELECT COUNT(*) FROM Submission WHERE :account=account AND :course=course AND points=true")
    Integer getPointsByAccountAndCourse(@Param("account") Account account, @Param("course") Course course);

    @Query("SELECT COUNT(*) FROM Submission WHERE :account=account AND :course=course AND :category=category AND points=true")
    public Integer getPointsByAccountAndCourseAncCategory(@Param("account")Account account, @Param("course") Course course, @Param("category") Category category);

    public List<Submission> findByAccountAndCourseAndCategoryAndTaskAndPoints(Account account, Course course, Category category, Task task, Boolean points);
}

package wepaht.SQLTasker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import wepaht.SQLTasker.domain.PastQuery;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wepaht.SQLTasker.domain.PointHolder;
import wepaht.SQLTasker.domain.Task;

@RestResource(exported = false)
public interface PastQueryRepository extends JpaRepository<PastQuery, Long>{


    List<PastQuery> findByTaskAndCorrectAndUsername(Task task, boolean correct, String username);

    List<PastQuery> findByTaskAndCorrect(Task task, boolean correct);

    List<PastQuery> findByTaskAndUsername(Task task, String username);

    List<PastQuery> findByCorrectAndUsername(boolean correct, String username);

    List<PastQuery> findByCorrectAndUsernameAndAwarded(boolean correct, String username, boolean awarded);

    List<PastQuery> findByCorrect(boolean correctness);
    List<PastQuery> findByTask(Task task);
    List<PastQuery> findByUsername(String username);
    
    @Query("SELECT username, COUNT(*) AS points FROM PastQuery WHERE awarded=true AND correct=true GROUP BY username")
    List<?> getPoints();
    
    @Query("SELECT COUNT(*) FROM PastQuery WHERE :username=username AND awarded=true AND correct=true")
    Integer getPointsByUsername(@Param("username") String username);
    
    @Query("SELECT p.task, p.awarded FROM PastQuery p WHERE :username=username")
    List<?> getExercisesAndAwardedByUsername(@Param("username")String username);
    
    //@Query(value = "select new SurveyAnswerStatistics(v.answer, count(v)) from Survey v group by v.answer")
    @Query("SELECT new wepaht.SQLTasker.domain.PointHolder(q.username, COUNT(q)) FROM PastQuery q WHERE q.awarded=true AND q.correct=true GROUP BY q.username")
    List<PointHolder> exportAllPoints();
}


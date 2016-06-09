package wepaht.SQLTasker.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.Submission;

public interface SubmissionRepository extends JpaRepository<Submission, Long>{
    
//    @Query("SELECT p.task.name, p.awarded FROM PastQuery p WHERE :username=username")
//    List<?> getExercisesAndAwardedByUsername(@Param("username")String username);
    
    @Query("SELECT s.task.name, s.points FROM Submission s WHERE :account=account")
    List<?> getTaskNameAndPointsByAccount(@Param("account") Account account);
    
//    @Query("SELECT COUNT(*) FROM PastQuery WHERE :username=username AND awarded=true AND correct=true")
//    Integer getPointsByUsername(@Param("username") String username);
    
    @Query("SELECT COUNT(*) FROM Submission WHERE :account=account AND points=true")
    Integer getPointsByAccount(@Param("account") Account account);
}

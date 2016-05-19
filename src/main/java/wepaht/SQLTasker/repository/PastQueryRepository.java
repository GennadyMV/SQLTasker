package wepaht.SQLTasker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import wepaht.SQLTasker.domain.PastQuery;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

@RestResource(exported = false)
public interface PastQueryRepository extends JpaRepository<PastQuery, Long>{


    List<PastQuery> findByTaskIdAndCorrectAndUsername(Long taskId, boolean correctness, String username);

    List<PastQuery> findByTaskIdAndCorrect(Long taskId, boolean correctness);

    List<PastQuery> findByTaskIdAndUsername(Long taskId, String username);

    List<PastQuery> findByCorrectAndUsername(boolean correctness, String username);

    List<PastQuery> findByCorrectAndUsernameAndAwarded(boolean correctness, String username, boolean canGetPoint);

    List<PastQuery> findByCorrect(boolean correctness);
    List<PastQuery> findByTaskId(Long taskId);
    List<PastQuery> findByUsername(String username);
    
    @Query("SELECT username, COUNT(*) AS points FROM PastQuery WHERE awarded=true AND correct=true GROUP BY username")
    List<?> getPoints();
}


package wepaht.SQLTasker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import wepaht.SQLTasker.domain.PastQuery;

import java.util.List;

@RestResource(exported = false)
public interface PastQueryRepository extends JpaRepository<PastQuery, Long>{


    List<PastQuery> findByTaskIdAndCorrectnessAndUsername(Long taskId, boolean correctness, String username);

    List<PastQuery> findByTaskIdAndCorrectness(Long taskId, boolean correctness);

    List<PastQuery> findByTaskIdAndUsername(Long taskId, String username);

    List<PastQuery> findByCorrectnessAndUsername(boolean correctness, String username);

    List<PastQuery> findByCorrectnessAndUsernameAndCanGetPoint(boolean correctness, String username, boolean canGetPoint);

    List<PastQuery> findByCorrectness(boolean correctness);
    List<PastQuery> findByTaskId(Long taskId);
    List<PastQuery> findByUsername(String username);
}


package wepaht.SQLTasker.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wepaht.SQLTasker.domain.TaskFeedback;

public interface TaskFeedbackRepository extends JpaRepository<TaskFeedback, Long> {
    
    
    @Override
    @Query("SELECT t FROM TaskFeedback t WHERE t.deleted=false")
    List<TaskFeedback> findAll();
    
    
    @Override
    @Query("SELECT t FROM TaskFeedback t WHERE :id=t.id AND t.deleted=false")
    TaskFeedback findOne(@Param("id")Long id);
}

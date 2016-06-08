package wepaht.SQLTasker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wepaht.SQLTasker.domain.Submission;

public interface SubmissionRepository extends JpaRepository<Submission, Long>{
    
}

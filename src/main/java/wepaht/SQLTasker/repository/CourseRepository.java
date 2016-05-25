package wepaht.SQLTasker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wepaht.SQLTasker.domain.Course;

public interface CourseRepository extends JpaRepository<Course, Long>{
    
}

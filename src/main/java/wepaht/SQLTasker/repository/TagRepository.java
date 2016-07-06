package wepaht.SQLTasker.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import wepaht.SQLTasker.domain.Tag;
import wepaht.SQLTasker.domain.Task;

public interface TagRepository extends JpaRepository<Tag,Long> {
    
    List findByTask(Task task);
    List findByName(String name);
    Tag findByNameAndTask(String name, Task task);
}

package wepaht.SQLTasker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import wepaht.SQLTasker.domain.Database;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;

@RestResource(exported = false)
public interface DatabaseRepository extends JpaRepository<Database, Long>{

    @Override
    @Query("SELECT d FROM Database d WHERE d.id=:id AND d.deleted=false")
    public Database findOne(@Param("id") Long id);
    
    @Override
    @Query("SELECT d FROM Database d WHERE d.deleted=false")    
    public List<Database> findAll();
    
    public List<Database> findByNameAndDeletedFalse(String name);

    public List<Database> findByOwnerAndDeletedFalse(TmcAccount owner);
}

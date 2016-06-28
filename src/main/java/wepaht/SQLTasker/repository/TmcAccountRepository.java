package wepaht.SQLTasker.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wepaht.SQLTasker.domain.TmcAccount;

public interface TmcAccountRepository extends JpaRepository<TmcAccount, Long>{
    
    @Override
    @Query("SELECT a FROM TmcAccount a WHERE a.id=:id AND a.deleted=false")
    TmcAccount findOne(@Param("id") Long id);
    
    @Query("SELECT a FROM TmcAccount a WHERE a.deleted=false")
    @Override
    List findAll();
    
    TmcAccount findByUsernameAndDeletedFalse(String username);
}

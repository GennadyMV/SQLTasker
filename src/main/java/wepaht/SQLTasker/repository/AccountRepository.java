package wepaht.SQLTasker.repository;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import wepaht.SQLTasker.domain.Account;

@RestResource(exported = false)
public interface AccountRepository extends JpaRepository<Account, Long>{

    @Override
    @Query("SELECT a FROM Account a WHERE a.id=:id AND a.deleted=false")
    Account findOne(@Param("id") Long id);
    
    @Override
    @Query("SELECT a FROM Account a WHERE a.deleted=false")
    List<Account> findAll();
    
    Account findByUsernameAndDeletedFalse(String username);
    List findByRoleAndDeletedFalse(String role);
}

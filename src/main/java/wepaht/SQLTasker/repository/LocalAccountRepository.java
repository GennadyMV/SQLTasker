package wepaht.SQLTasker.repository;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import wepaht.SQLTasker.domain.LocalAccount;

@RestResource(exported = false)
public interface LocalAccountRepository extends JpaRepository<LocalAccount, Long>{

    @Override
    @Query("SELECT a FROM LocalAccount a WHERE a.id=:id AND a.deleted=false")
    LocalAccount findOne(@Param("id") Long id);
    
    @Override
    @Query("SELECT a FROM LocalAccount a WHERE a.deleted=false")
    List<LocalAccount> findAll();
    
    LocalAccount findByUsernameAndDeletedFalse(String username);
    List findByAccountRoleAndDeletedFalse(String accountRole);
}

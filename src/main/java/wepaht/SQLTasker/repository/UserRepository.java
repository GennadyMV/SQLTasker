package wepaht.SQLTasker.repository;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import wepaht.SQLTasker.domain.Account;

@RestResource(exported = false)
public interface UserRepository extends JpaRepository<Account, Long>{

    Account findByUsername(String username);
    List findByRole(String role);
}

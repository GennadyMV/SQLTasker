package wepaht.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import wepaht.domain.AuthenticationToken;
import wepaht.domain.Account;

@RestResource(exported = false)
public interface AuthenticationTokenRepository extends JpaRepository<AuthenticationToken, Long>{

    AuthenticationToken findByUser(Account user);
    AuthenticationToken findByToken(String token);
}

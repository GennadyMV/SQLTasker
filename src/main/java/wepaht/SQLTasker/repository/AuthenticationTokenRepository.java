package wepaht.SQLTasker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import wepaht.SQLTasker.domain.AuthenticationToken;
import wepaht.SQLTasker.domain.Account;

@RestResource(exported = false)
public interface AuthenticationTokenRepository extends JpaRepository<AuthenticationToken, Long>{

    AuthenticationToken findByUser(Account user);
    AuthenticationToken findByToken(String token);
}

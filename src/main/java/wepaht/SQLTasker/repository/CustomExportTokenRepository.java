package wepaht.SQLTasker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import wepaht.SQLTasker.domain.CustomExportToken;
import wepaht.SQLTasker.domain.LocalAccount;
import wepaht.SQLTasker.domain.TmcAccount;

@RestResource(exported = false)
public interface CustomExportTokenRepository extends JpaRepository<CustomExportToken, Long>{

    CustomExportToken findByUser(TmcAccount user);
    CustomExportToken findByToken(String token);
}

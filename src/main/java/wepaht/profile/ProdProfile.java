package wepaht.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import wepaht.domain.User;
import wepaht.repository.UserRepository;

import javax.annotation.PostConstruct;

@Configuration
@Profile(value = "prod")
public class ProdProfile {

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        User admin = new User();
        admin.setRole("ADMIN");
        admin.setUsername("admin");
        admin.setPassword("admin");
        userRepository.save(admin);
    }
}

package wepaht.SQLTasker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import wepaht.SQLTasker.profile.DevProfile;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@Import({DevProfile.class})
        public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

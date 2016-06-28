package wepaht.SQLTasker.service;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wepaht.SQLTasker.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class TaskServiceTest {
    
    @InjectMocks
    TaskService taskService;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
}

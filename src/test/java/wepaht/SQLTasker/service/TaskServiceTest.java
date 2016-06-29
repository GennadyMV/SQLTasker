package wepaht.SQLTasker.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wepaht.SQLTasker.Application;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;
import wepaht.SQLTasker.repository.TaskRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class TaskServiceTest {
    
    @Mock
    AccountService accountServiceMock;
    
    @Mock
    DatabaseService dbServiceMock;
    
    @Mock
    TaskRepository taskRepoMock;
    
    @InjectMocks
    TaskService taskService;
    
    private Database dbMock;
    private TmcAccount accountMock;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        dbMock = mock(Database.class);
        accountMock = mock(TmcAccount.class);
        
        when(accountServiceMock.getAuthenticatedUser()).thenReturn(accountMock);
        
    }
    
    @Test
    public void userCanCreateTask() {
        Task testTask = new Task();
        testTask.setName("Moustache");
        testTask.setSolution("SELECT 42");
        
        when(dbMock.getId()).thenReturn(42l);
        when(dbServiceMock.getDatabase(eq(42l))).thenReturn(dbMock);
        when(dbServiceMock.isValidQuery(eq(dbMock), any())).thenReturn(Boolean.TRUE);
        
        taskService.createTask(null, testTask, 42l, null, null);
        
        verify(taskRepoMock, times(1)).save(eq(testTask));
    }
}

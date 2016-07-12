package wepaht.SQLTasker.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wepaht.SQLTasker.Application;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.domain.Tag;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;
import wepaht.SQLTasker.constant.ConstantString;
import static wepaht.SQLTasker.constant.ConstantString.*;
import wepaht.SQLTasker.repository.TaskRepository;

@RunWith(MockitoJUnitRunner.class)
@SpringApplicationConfiguration(Application.class)
public class TaskServiceTest {
    
    @Mock
    TagService tagServiceMock;
    
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
        
        dbMock = mock(Database.class);
        accountMock = mock(TmcAccount.class);
        
        when(accountServiceMock.getAuthenticatedUser()).thenReturn(accountMock);
        
    }
    
    @Test
    public void teacherCanCreateTask() {
        Task testTask = new Task();
        testTask.setName("Moustache");
        testTask.setSolution("SELECT 42");
        
        when(dbMock.getId()).thenReturn(42l);
        when(dbServiceMock.getDatabase(eq(42l))).thenReturn(dbMock);
        when(dbServiceMock.isValidQuery(eq(dbMock), any())).thenReturn(Boolean.TRUE);
        when(accountMock.getRole()).thenReturn(ROLE_TEACHER);
        
        taskService.createTask(null, testTask, 42l, null, null);
        
        verify(taskRepoMock, times(1)).save(eq(testTask));
    }
    
    @Test
    public void teacherCanCreateTag() {
        Task task = new Task();
        task.setName("Awesome");
        String tagName = "Swag";
        when(taskRepoMock.findOne(any(Long.class))).thenReturn(task);
        when(accountMock.getRole()).thenReturn(ROLE_TEACHER);
        
        taskService.addTag(null, Long.MIN_VALUE, tagName);
        verify(tagServiceMock, times(1)).createTag(eq(tagName), eq(task));
    }
    
    @Test
    public void teacherCanDeleteTag() {
        Task task = new Task();
        task.setName("Awesome");
        String tagName = "Swag";
        Tag tag = new Tag();
        tag.setName(tagName);
        tag.setTask(task);
        when(taskRepoMock.findOne(any(Long.class))).thenReturn(task);
        when(accountMock.getRole()).thenReturn(ROLE_TEACHER);
        when(tagServiceMock.getTagByNameAndTask(eq(tagName), eq(task))).thenReturn(tag);
        
        taskService.deleteTag(null, Long.MIN_VALUE, tagName);
        verify(tagServiceMock, times(1)).getTagByNameAndTask(tagName, task);
        verify(tagServiceMock, times(1)).deleteTag(tag);
    }
    
    @Test
    public void teacherCanEditTask() {
        Task task = new Task();
        task.setName("Edit me!");
        Database otherDb = mock(Database.class);
        task.setDatabase(otherDb);
        task.setSolution("SELECT 42;");
        Long taskId = 42l;
        Long databaseId = 43l;
        String newName = "Gotcha!";
        String newSolution = "SELECT 45;";
        
        when(accountMock.getRole()).thenReturn(ROLE_TEACHER);
        when(taskRepoMock.findOne(any(Long.class))).thenReturn(task);
        when(dbServiceMock.getDatabase(eq(databaseId))).thenReturn(dbMock);
        when(dbServiceMock.isValidQuery(eq(dbMock), eq(newSolution))).thenReturn(true);
        
        taskService.editTask(null, taskId, databaseId, newName, newSolution, "I described it!");
        
        assertEquals(newName, task.getName());
        assertEquals(newSolution, task.getSolution());
        assertTrue(task.getDescription() != null);
    }
    
    public void teacherCanDeleteTask() {
        Task task = new Task();
        task.setName("Hurr");
        Long taskId = 42l;
        
        
        taskService.removeTask(null, Long.MIN_VALUE);
        
        assertTrue(task.getDeleted());
    }
}

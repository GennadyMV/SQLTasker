package wepaht.SQLTasker.service;

import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wepaht.SQLTasker.Application;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;
import static wepaht.SQLTasker.library.ConstantString.ROLE_STUDENT;
import static wepaht.SQLTasker.library.ConstantString.ROLE_TEACHER;
import wepaht.SQLTasker.repository.CategoryRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class CategoryServiceTest {
    
    @Mock
    AccountService accountServiceMock;
    
    @Mock
    CategoryRepository catRepoMock;
    
    @InjectMocks
    CategoryService categoryService;
    
    private Task taskMock1;
    private Task taskMock2;
    private Category catMock;
    private Category realCat;
    private TmcAccount accountMock;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        taskMock1 = mock(Task.class);
        taskMock2 = mock(Task.class);
        catMock = mock(Category.class);
        realCat = new Category();  
        accountMock = mock(TmcAccount.class);
    }
    
    @Test
    public void testGetNextTaskReturnsNextTask() {
        when(catMock.getTaskList()).thenReturn(Arrays.asList(taskMock1, taskMock2));
        
        assertEquals(taskMock2, categoryService.getNextTask(catMock, taskMock1));
    }
    
    @Test
    public void testSetCategoryTaskFurtherReordersTasks() {
        realCat.setTaskList(Arrays.asList(taskMock1, taskMock2));
        
        categoryService.setCategoryTaskFurther(realCat, taskMock1);
        assertEquals(taskMock1, realCat.getTaskList().get(1));
    }
    
    @Test
    public void testTeacherCanEditCategory() {
        realCat.setName("Not edited");
        realCat.setDescription("Whee");
        when(catRepoMock.findOne(any(Long.class))).thenReturn(realCat);
        when(accountMock.getRole()).thenReturn(ROLE_TEACHER);
        when(accountServiceMock.getAuthenticatedUser()).thenReturn(accountMock);
        String expected = "Edited";
        
        categoryService.updateCategory(null, Long.MIN_VALUE, expected, "High noon", null);
        
        assertEquals(expected, realCat.getName());
    }
    
    @Test
    public void testStudentCanNotEditCategoryWithoutOwnership() {
        String expected = "Not edited";
        realCat.setName(expected);
        realCat.setDescription("Whee!");
        when(catRepoMock.findOne(any(Long.class))).thenReturn(realCat);
        when(accountMock.getRole()).thenReturn(ROLE_STUDENT);
        when(accountServiceMock.getAuthenticatedUser()).thenReturn(accountMock);
        
        categoryService.updateCategory(null, Long.MIN_VALUE, "Something else", "High noon", null);
        
        assertEquals(expected, realCat.getName());
    }
    
    @Test
    public void testStudentCanEditOwnCategory() {
        realCat.setName("Not edited");
        realCat.setDescription("Whee!");
        when(accountMock.getRole()).thenReturn(ROLE_STUDENT);
        realCat.setOwner(accountMock);
        when(catRepoMock.findOne(any(Long.class))).thenReturn(realCat);        
        when(accountServiceMock.getAuthenticatedUser()).thenReturn(accountMock);
        String expected = "Edited";
        
        categoryService.updateCategory(null, Long.MIN_VALUE, expected, "High noon", null);
        
        assertEquals(expected, realCat.getName());
    }
}

package wepaht.SQLTasker.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wepaht.SQLTasker.Application;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;
import static wepaht.SQLTasker.library.ConstantString.*;
import wepaht.SQLTasker.repository.CourseRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class CourseServiceTest {
    
    @Mock
    CourseRepository courseRepoMock;
    
    @Mock
    AccountService accountServiceMock;
    
    @Mock
    TaskService taskServiceMock;
    
    @Mock
    CategoryService categoryServiceMock;
    
    @Mock
    CategoryDetailService catDetailServiceMock;
    
    @InjectMocks
    CourseService courseService;
    
    private Course course;
    private TmcAccount accountMock;
    private Category categoryMock;
    private Task taskMock;
    private TmcAccount realAccount;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        course = new Course();
        course.setName("Test course");
        course.setStudents(new ArrayList<>());
        accountMock = mock(TmcAccount.class);
        categoryMock = mock(Category.class);
        taskMock = mock(Task.class);
        realAccount = new TmcAccount();
        
        when(accountMock.getUsername()).thenReturn("joiner");
        when(accountMock.getRole()).thenReturn(ROLE_STUDENT);
        when(accountMock.getId()).thenReturn(42l);
        when(accountServiceMock.getAuthenticatedUser()).thenReturn(accountMock);
    }
    
    @Test
    public void testUserCanJoinCourse() {
        when(courseRepoMock.findOne(any(Long.class))).thenReturn(course);
        
        courseService.joinCourse(null, 42l);
        
        assertTrue(course.getStudents().contains(accountMock));
    }
    
    @Test
    public void testUserCanLeaveCourse() {
        when(courseRepoMock.findOne(any(Long.class))).thenReturn(course);        
        realAccount.setRole(ROLE_STUDENT);
        realAccount.setUsername("Esko");
        when(accountServiceMock.getAuthenticatedUser()).thenReturn(realAccount);
        courseService.joinCourse(null, Long.MIN_VALUE);
        
        courseService.leaveCourse(null, Long.MIN_VALUE);
        
        assertFalse(course.getStudents().contains(realAccount));
    }
    
    @Test
    public void testTaskIsAccessableThroughCourseAndCategory() {
        course = new Course();
        course.setName("Avenge me!");
        course.setCourseCategories(Arrays.asList(categoryMock));
        
        when(courseRepoMock.findOne(any(Long.class))).thenReturn(course);
        when(categoryMock.getId()).thenReturn(1l);
        when(categoryServiceMock.getCategoryById(1l)).thenReturn(categoryMock);
        when(catDetailServiceMock.isCategoryActive(course, categoryMock)).thenReturn(Boolean.TRUE);
        when(categoryServiceMock.categoryHasTask(any(), any())).thenReturn(Boolean.TRUE);
        when(taskMock.getId()).thenReturn(2l);
        when(taskServiceMock.getTaskById(2l)).thenReturn(taskMock);
        
        assertEquals(VIEW_TASK, courseService.getCourseCategoryTask(null, null, Long.MIN_VALUE, 1l, 2l));
    }
    
    @Test
    public void testQueryCanBeSentToTaskAccessedThroughCourseAndCategory() {
        String query = "SELECT 42";
        course = new Course();
        course.setName("Queries, everywhere");
        course.setCourseCategories(Arrays.asList(categoryMock));
        course.setStudents(Arrays.asList(accountMock));
        
        when(courseRepoMock.findOne(1l)).thenReturn(course);
        when(taskServiceMock.performQueryToTask(any(), any(), any(), any(), any())).thenReturn(Arrays.asList("hurr", "durr", Boolean.TRUE));
        
        courseService.createQuery(null, query, 1l, 2l, 3l);
        
        verify(taskServiceMock, times(1)).performQueryToTask(any(), eq(3l), eq(query), eq(2l), eq(1l));
    }
    
    @Test
    public void testCourseCanBeDeletedByTeacher() {
        Course course = new Course();
        course.setName("Destrooy");
        when(accountMock.getRole()).thenReturn(ROLE_TEACHER);
        when(courseRepoMock.findOne(any(Long.class))).thenReturn(course);
        
        courseService.deleteCourse(null, Long.MIN_VALUE);
        
        assertTrue(course.getDeleted());
    }
    
    @Test
    public void testTeacherCanCreateCourse() {
        when(accountMock.getRole()).thenReturn(ROLE_TEACHER);
        
        courseService.createCourse(null, course.getName(), null, null, course.getDescription(), null);
        
        verify(courseRepoMock, times(1)).save(any(Course.class));
    }
    
    @Test
    public void testCourseCanBeEdited() {
        course = new Course();
        course.setName("Is a course");
        course.setDescription("Will be edited");
        
        when(accountMock.getRole()).thenReturn(ROLE_TEACHER);
        when(courseRepoMock.findOne(any(Long.class))).thenReturn(course);
        
        String newName = "Now edited!";
        String newDescription = "Told you";
        courseService.editCourse(null, Long.MIN_VALUE, "Now edited!", null, null, "Told you", null);
        
        assertTrue(course.getName().equals(newName) && course.getDescription().equals(newDescription));
    }
    
    @Test
    public void testInvalidDatesCannotBeSet() {
        LocalDate date = LocalDate.now();
        LocalDate invalidDate = date.minusYears(1l);
        course = courseService.setCourseAttributes("Whee", "Durr", null, date.toString(), invalidDate.toString(), new ArrayList<>());
        
        assertTrue(course.getExpires() == null);
    }
}

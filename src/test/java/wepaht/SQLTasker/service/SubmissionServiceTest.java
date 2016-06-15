/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.SQLTasker.service;

import java.time.LocalDate;
import java.util.Arrays;
import static org.hamcrest.Matchers.any;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wepaht.SQLTasker.Application;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.CategoryDetail;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Submission;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.repository.SubmissionRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class SubmissionServiceTest {
    
    @Mock
    SubmissionRepository submissionRepository;
    
    @Mock
    AccountService accountServiceMock;
    
    @Mock
    TaskResultService resultService;
    
    @Mock
    CategoryDetailService detailService;
    
    @Autowired
    @InjectMocks
    SubmissionService submissionService;
    
    private Task testTask;
    private Category testCategory;
    private Course testCourse;
    private Account testAccount;
    private CategoryDetail testDetail;
    
    public SubmissionServiceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        testTask = mock(Task.class);
        testCategory = mock(Category.class);
        testCourse = mock(Course.class);
        testAccount = mock(Account.class);
        testDetail = mock(CategoryDetail.class);
    }
    
    @After
    public void tearDown() {
    }

    

    /**
     * Test of createNewSubmissionAndCheckPoints method, of class SubmissionService.
     */
    @Test
    public void testCreateNewSubmissionAndCheckPoints() {
        when(testTask.getSolution()).thenReturn("SELECT 1");
        when(resultService.evaluateSubmittedQueryResult(testTask, "SELECT 1")).thenReturn(Boolean.TRUE);
        when(testCourse.getStarts()).thenReturn(LocalDate.MIN);
        when(testCourse.getExpires()).thenReturn(LocalDate.MAX);
        when(detailService.getCategoryDetailsByCourseAndCategory(testCourse, testCategory)).thenReturn(Arrays.asList(testDetail));
        when(testDetail.getStarts()).thenReturn(LocalDate.MIN);
        when(testDetail.getExpires()).thenReturn(LocalDate.MAX);
        
        assertTrue(submissionService.createNewSubmissionAndCheckPoints(testTask, "SELECT 1", testCategory, testCourse));
    }
    
    @Test
    public void testCreateNewSubmissionAndCheckPointsWhenCategoryIsNotActive() {
        when(testTask.getSolution()).thenReturn("SELECT 1");
        when(resultService.evaluateSubmittedQueryResult(testTask, "SELECT 1")).thenReturn(Boolean.TRUE);
        when(testCourse.getStarts()).thenReturn(LocalDate.MIN);
        when(testCourse.getExpires()).thenReturn(LocalDate.MAX);
        when(detailService.getCategoryDetailsByCourseAndCategory(testCourse, testCategory)).thenReturn(Arrays.asList(testDetail));
        
        LocalDate date = LocalDate.now();
        date = date.plusDays(1l);
        when(testDetail.getStarts()).thenReturn(date);
        when(testDetail.getExpires()).thenReturn(LocalDate.MAX);
        
        assertFalse(submissionService.createNewSubmissionAndCheckPoints(testTask, "SELECT 1", testCategory, testCourse));
    }
    
    @Test
    public void testCreateNewSubmissionAndCheckPoitsWhenCourseIsNotActive() {
        when(testTask.getSolution()).thenReturn("SELECT 1");
        when(resultService.evaluateSubmittedQueryResult(testTask, "SELECT 1")).thenReturn(Boolean.TRUE);
        
        LocalDate date = LocalDate.now();
        date = date.plusDays(1l);
        when(testCourse.getStarts()).thenReturn(date);
        when(testCourse.getExpires()).thenReturn(LocalDate.MAX);
        
        when(detailService.getCategoryDetailsByCourseAndCategory(testCourse, testCategory)).thenReturn(Arrays.asList(testDetail));
        when(testDetail.getStarts()).thenReturn(LocalDate.MIN);
        when(testDetail.getExpires()).thenReturn(LocalDate.MAX);
        
        assertFalse(submissionService.createNewSubmissionAndCheckPoints(testTask, "SELECT 1", testCategory, testCourse));
    }
}

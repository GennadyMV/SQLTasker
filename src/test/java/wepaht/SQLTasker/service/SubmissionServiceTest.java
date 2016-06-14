/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.SQLTasker.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wepaht.SQLTasker.Application;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.CategoryDetail;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.domain.Submission;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.repository.AccountRepository;
import wepaht.SQLTasker.repository.CategoryDetailRepository;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.CourseRepository;
import wepaht.SQLTasker.repository.DatabaseRepository;
import wepaht.SQLTasker.repository.SubmissionRepository;
import wepaht.SQLTasker.repository.TaskRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class SubmissionServiceTest {
    
    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    DatabaseRepository databaseRepository;
    
    @Autowired
    TaskRepository taskRepository;
    
    @Autowired
    CategoryRepository categoryRepository;
    
    @Autowired
    CourseRepository courseRepository;
    
    @Autowired
    CategoryDetailRepository categoryDetailRepository;
    
    @Autowired
    SubmissionRepository submissionRepository;
    
    @Mock
    AccountService accountServiceMock;
    
    @Autowired
    @InjectMocks
    SubmissionService submissionService;
    
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
    }
    
    @After
    public void tearDown() {
    }

    

    /**
     * Test of createNewSubmissionAndCheckPoints method, of class SubmissionService.
     */
    @Test
    public void testCreateNewSubmissionAndCheckPoints() {
        Task task = createTestTask(createTestDatabase("test database"), "is a task");
        Category category = createTestCategory(task, "a category");
        Account account = createTestAccount("tester");
        Course course = createTestCourse(account, category, "Whoop", LocalDate.MIN, LocalDate.MAX);
        createTestCategoryDetail(course, category, LocalDate.MIN, LocalDate.MAX);
        
        when(accountServiceMock.getAuthenticatedUser()).thenReturn(account);
        
        submissionService.createNewSubmissionAndCheckPoints(task, "SELECT 1", category, course);
        
        List<Submission> submissions = submissionRepository.findByAccountAndCourseAndCategoryAndTaskAndPoints(account, course, category, task, Boolean.TRUE);
        assertTrue(submissions.size() > 0);
    }

    private void createTestCategoryDetail(Course course, Category category, LocalDate starts, LocalDate expires) {
        CategoryDetail detail = new CategoryDetail(course, category, starts, expires);
        detail = categoryDetailRepository.save(detail);
    }

    private Course createTestCourse(Account account, Category category, String name, LocalDate starts, LocalDate expires) {
        Course course = new Course();
        course.setName(name);
        course.setStarts(starts);
        course.setExpires(expires);
        course.setStudents(Arrays.asList(account));
        course.setCourseCategories(Arrays.asList(category));
        course = courseRepository.save(course);
        return course;
    }

    private Category createTestCategory(Task task, String name) {
        Category category = new Category();
        category.setName(name);
        category.setTaskList(Arrays.asList(task));
        category = categoryRepository.save(category);
        return category;
    }

    private Task createTestTask(Database database, String name) {
        Task task = new Task();
        task.setName(name);
        task.setDatabase(database);
        task.setSolution("SELECT 1");
        task = taskRepository.save(task);
        return task;
    }

    private Database createTestDatabase(String name) {
        Database database = new Database();
        database.setName(name);
        database.setDatabaseSchema("CREATE TABLE Persons"
                + "(PersonID int, LastName varchar(255), FirstName varchar(255), Address varchar(255), City varchar(255));"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (2, 'Raty', 'Matti', 'Rautalammintie', 'Helsinki');"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (1, 'Jaaskelainen', 'Timo', 'Jossakin', 'Heslinki');"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (3, 'Entieda', 'Kake?', 'Laiva', 'KJYR');");
        database = databaseRepository.save(database);
        return database;
    }

    private Account createTestAccount(String name) {
        Account account = new Account();
        account.setUsername(name);
        account.setPassword(name);
        account.setRole("STUDENT");
        account = accountRepository.save(account);
        return account;
    }
    
}

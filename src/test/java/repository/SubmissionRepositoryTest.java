package repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import wepaht.SQLTasker.Application;
import static wepaht.SQLTasker.constant.ConstantString.ROLE_STUDENT;
import static wepaht.SQLTasker.constant.ConstantString.ROLE_TEACHER;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.domain.Submission;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.CourseRepository;
import wepaht.SQLTasker.repository.DatabaseRepository;
import wepaht.SQLTasker.repository.SubmissionRepository;
import wepaht.SQLTasker.repository.TaskRepository;
import wepaht.SQLTasker.repository.TmcAccountRepository;
import static wepaht.SQLTasker.specification.SubmissionSpecification.searchSubmissions;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
public class SubmissionRepositoryTest {

    @Autowired
    DatabaseRepository dbRepoTest;

    @Autowired
    TaskRepository taskRepoTest;

    @Autowired
    CategoryRepository catRepoTest;

    @Autowired
    CourseRepository courseRepoTest;

    @Autowired
    TmcAccountRepository accRepoTest;

    @Autowired
    SubmissionRepository subRepo;

    TmcAccount userTest;
    TmcAccount userTest2;
    Database dbTest;
    Task taskTest;
    Category catTest;
    Course courseTest;
    String queryTest = "SHOW TABLES;";
    private Course courseTest2;
    private Category catTest2;
    private Task taskTest2;

    @Before
    public void init() {

        userTest = new TmcAccount();
        userTest.setUsername("Foo Fighter");
        userTest.setRole(ROLE_TEACHER);
        userTest = accRepoTest.save(userTest);

        userTest2 = new TmcAccount();
        userTest2.setUsername("Foo Defender");
        userTest2.setRole(ROLE_TEACHER);
        userTest2 = accRepoTest.save(userTest2);

        dbTest = new Database();
        dbTest.setDatabaseSchema("CREATE TABLE Foo(id integer);\n"
                + "INSERT INTO Foo (id) VALUES (7);");
        dbTest.setOwner(userTest);
        dbTest.setName("Foo Base");
        dbTest = dbRepoTest.save(dbTest);

        taskTest = new Task();
        taskTest.setDatabase(dbTest);
        taskTest.setName("Foo Task");
        taskTest.setOwner(userTest);
        taskTest.setSolution("SHOW TABLES;");
        taskTest = taskRepoTest.save(taskTest);
        
        taskTest2 = new Task();
        taskTest2.setDatabase(dbTest);
        taskTest2.setName("Fooer Task");
        taskTest2.setOwner(userTest);
        taskTest2.setSolution("SHOW TABLES;");
        taskTest2 = taskRepoTest.save(taskTest2);

        catTest = new Category();
        catTest.setName("Foo Category");
        catTest.setOwner(userTest);
        catTest = catRepoTest.save(catTest);

        catTest2 = new Category();
        catTest2.setName("Fooer Category");
        catTest2.setOwner(userTest);
        catTest2 = catRepoTest.save(catTest2);

        courseTest = new Course();
        courseTest.setName("Foo Course");
        courseTest = courseRepoTest.save(courseTest);

        courseTest2 = new Course();
        courseTest2.setName("Fooer Course");
        courseTest2 = courseRepoTest.save(courseTest2);
    }
    
    @After
    public void after() {
        subRepo.deleteAll();
    }

    @Test
    @Transactional
    public void canFindByCourseTest() {
        int sizeBefore = subRepo.findAll(searchSubmissions(Arrays.asList(courseTest), null, null, null, null, null, null)).size();
        
        subRepo.save(new Submission(userTest, taskTest, catTest, courseTest, queryTest, Boolean.TRUE));
        subRepo.save(new Submission(userTest, taskTest, catTest, courseTest2, queryTest, Boolean.TRUE));
        
        List<Submission> subs = subRepo.findAll(searchSubmissions(Arrays.asList(courseTest), null, null, null, null, null, null));        

        assertEquals(sizeBefore + 1, subs.size());
    }

    @Test
    @Transactional
    public void canFindByCategoryTest() {
        int sizeBefore = subRepo.findAll(searchSubmissions(null, Arrays.asList(catTest), null, null, null, null, null)).size();
        subRepo.save(new Submission(userTest, taskTest, catTest, courseTest, queryTest, Boolean.TRUE));
        subRepo.save(new Submission(userTest, taskTest, catTest2, courseTest, queryTest, Boolean.TRUE));
        
        assertEquals(sizeBefore + 1, subRepo.findAll(searchSubmissions(null, Arrays.asList(catTest), null, null, null, null, null)).size());
    }
    
    @Test
    @Transactional
    public void canFindByTaskTest() {
        int sizeBefore = subRepo.findAll(searchSubmissions(null, null, Arrays.asList(taskTest), null, null, null, null)).size();
        subRepo.save(new Submission(userTest, taskTest, catTest, courseTest, queryTest, Boolean.TRUE));
        subRepo.save(new Submission(userTest, taskTest2, catTest, courseTest, queryTest, Boolean.TRUE));
        
        assertEquals(sizeBefore + 1, subRepo.findAll(searchSubmissions(null, null, Arrays.asList(taskTest), null, null, null, null)).size());
    }

    @Test
    @Transactional
    public void canFindByUserTest() {
        int sizeBefore = subRepo.findAll(searchSubmissions(null, null, null, Arrays.asList(userTest), null, null, null)).size();
        subRepo.save(new Submission(userTest, taskTest, catTest, courseTest, queryTest, Boolean.TRUE));
        subRepo.save(new Submission(userTest2, taskTest, catTest, courseTest, queryTest, Boolean.TRUE));

        assertEquals(sizeBefore + 1, subRepo.findAll(searchSubmissions(null, null, null, Arrays.asList(userTest), null, null, null)).size());
    }
    
    @Test
    @Transactional
    public void canFindByBeforeTest() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        long sizeBefore = subRepo.count(searchSubmissions(null, null, null, null, null, null, LocalDateTime.now()));
        
        subRepo.save(new Submission(userTest, taskTest, catTest, courseTest, queryTest, Boolean.TRUE, yesterday));
        subRepo.save(new Submission(userTest, taskTest, catTest, courseTest, queryTest, Boolean.TRUE, tomorrow));
        
        
        assertEquals(1, Long.compare(subRepo.count(searchSubmissions(null, null, null, null, null, null, LocalDateTime.now())), sizeBefore));
    }
}

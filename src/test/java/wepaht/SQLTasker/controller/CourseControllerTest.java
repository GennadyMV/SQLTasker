package wepaht.SQLTasker.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import wepaht.SQLTasker.Application;
import wepaht.SQLTasker.service.AccountService;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.CategoryDetail;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.repository.AccountRepository;
import wepaht.SQLTasker.repository.CategoryDetailRepository;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.CourseRepository;
import wepaht.SQLTasker.repository.DatabaseRepository;
import wepaht.SQLTasker.repository.SubmissionRepository;
import wepaht.SQLTasker.repository.TaskRepository;
import wepaht.SQLTasker.service.CourseService;

@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class CourseControllerTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseService courseService;
    
    @Autowired
    DatabaseRepository databaseRepository;
    
    @Autowired
    SubmissionRepository submissionrepository;
    
    @Autowired
    TaskRepository taskRepository;

    @Autowired
    private CategoryDetailRepository categoryDetailsRepository;

    @Autowired
    private WebApplicationContext webAppContext;

    @Mock
    AccountService accountServiceMock;

    @InjectMocks
    CourseController courseController;

    private MockMvc mockMvc;
    private String URI = "/courses";
    private Account student;
    private Account teacher;

    public CourseControllerTest() {
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

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).apply(springSecurity()).build();

        accountRepository.deleteAll();
        student = new Account();
        student.setUsername("student");
        student.setPassword("student");
        student.setRole("STUDENT");
        student = accountRepository.save(student);

        teacher = new Account();
        teacher.setUsername("teacher");
        teacher.setPassword("teacher");
        teacher.setRole("TEACHER");
        teacher = accountRepository.save(teacher);
    }

    @After
    public void tearDown() {
        categoryDetailsRepository.deleteAll();
        submissionrepository.deleteAll();
        courseRepository.deleteAll();
        categoryRepository.deleteAll();
        accountRepository.deleteAll();
    }

    private Course createTestCourse(String name) {
        Course course = new Course();
        course.setName(name);

        return courseRepository.save(course);
    }

    private Category createTestCategory(String name) {
        Category category = new Category();
        category.setName(name);

        return categoryRepository.save(category);
    }       
    
    private Database createTestDatabase(String name) {
        Database database = new Database();
        database.setName(name);
        database.setDatabaseSchema("CREATE TABLE Foo(id integer);INSERT INTO Foo (id) VALUES (7);");
        database = databaseRepository.save(database);
        return database;
    }
    
    private Task createTestTask(String name, Database database) {
        Task task = new Task();
        task.setName(name);
        task.setDatabase(database);
        task.setSolution("SELECT 1;");
        return taskRepository.save(task);
    }

    private Category createTestCategory(String name, List<Task> tasks) {
        Category category = new Category();
        category.setName(name);
        category.setTaskList(tasks);

        return categoryRepository.save(category);
    }

    @Test
    public void testGetCoursesIsOk() throws Exception {
        mockMvc.perform(get(URI).with(user("stud").roles("STUDENT")).with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testGetCoursesListsCourses() throws Exception {
        Course course1 = createTestCourse("listing courses1");
        Course course2 = createTestCourse("listing courses2");

        mockMvc.perform(get(URI)
                .with(user("stud").roles("STUDENT"))
                .with(csrf()))
                .andExpect(model().attribute("courses", hasSize(2)))
                .andExpect(model().attribute("courses", Matchers.hasItem(
                                        Matchers.allOf(Matchers.hasProperty("id", Matchers.is(course1.getId()))))))
                .andExpect(model().attribute("courses", Matchers.hasItem(
                                        Matchers.allOf(Matchers.hasProperty("id", Matchers.is(course2.getId()))))))
                .andReturn();
    }

    @Test
    public void testTeacherCanCreateCourse() throws Exception {
        String name = "create course";

        mockMvc.perform(post(URI).param("name", name)
                .with(user("teach").roles("TEACHER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        assertTrue(courseRepository.findAll().stream()
                .filter(course -> course.getName().equals(name))
                .findFirst().isPresent());
    }

    @Test
    public void testTeacherCanCreateCourseWithAllParameters() throws Exception {
        String name = "Create reel course";
        String description = "Stuffs happen";
        String starts = LocalDate.MIN.toString();
        String expires = LocalDate.MAX.toString();

        Category category = createTestCategory("test category");

        mockMvc.perform(post(URI)
                .param("name", name)
                .param("description", description)
                .param("starts", starts)
                .param("expires", expires)
                .param("categoryIds", category.getId().toString())
                .with(user("teacher").roles("TEACHER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        assertTrue(courseRepository.findAll().stream()
                .filter(course -> course.getName().equals(name))
                .findFirst().isPresent());
    }

    @Test
    public void testTeacherCanNotSetInvalidExpireDate() throws Exception {
        String name = "Incorrect dates";
        String starts = "01-01-2017";
        String expires = "01-01-2016";

        mockMvc.perform(post(URI)
                .param("name", name)
                .param("starts", starts)
                .param("expires", expires)
                .with(user("teacher").roles("TEACHER")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        assertTrue(courseRepository.findAll().stream()
                .filter(course -> course.getName().equals(name) && course.getExpires() == null)
                .findFirst().isPresent());
    }

    @Test
    public void testCourseCanBeSeen() throws Exception {
        Course course = createTestCourse("Lookin at dis");

        mockMvc.perform(get(URI + "/" + course.getId())
                .with(user("student").roles("STUDENT")).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("course", course))
                .andReturn();
    }

    @Test
    public void testCourseCanBeDeleted() throws Exception {
        Course course = createTestCourse("Deleting course");

        mockMvc.perform(delete(URI + "/" + course.getId() + "/delete")
                .with(user("teacher").roles("TEACHER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        assertTrue(courseRepository.findOne(course.getId()) == null);
    }

    @Test
    public void testCourseCanBeDeletedWithCourses() throws Exception {
        Category category = createTestCategory("I'm a friend");
        Course course = new Course();
        course.setName("Deleting course with category");
        course.setCourseCategories(Arrays.asList(category));
        course = courseRepository.save(course);

        mockMvc.perform(delete(URI + "/" + course.getId() + "/delete")
                .with(user("teacher").roles("TEACHER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        assertTrue(courseRepository.findOne(course.getId()) == null);
    }

    @Test
    public void testCourseEditFormCanBeSeen() throws Exception {
        Course course = createTestCourse("Spoon");

        mockMvc.perform(get(URI + "/" + course.getId() + "/edit")
                .with(user("teacher").roles("TEACHER"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("course", course))
                .andReturn();
    }

    @Test
    public void testCourseCanBeEdited() throws Exception {
        Course course = createTestCourse("Editing!");
        Category category = createTestCategory("Hurr");
        String name = "Edited!";
        String description = "It is now edited";
        LocalDate starts = LocalDate.now();
        LocalDate expires = LocalDate.now().plusDays(8l);
        String categoryIds = category.getId().toString();

        mockMvc.perform(post(URI + "/" + course.getId() + "/edit")
                .param("name", name)
                .param("description", description)
                .param("starts", starts.toString())
                .param("expires", expires.toString())
                .param("categoryIds", categoryIds)
                .with(user("teacher").roles("TEACHER")).with(csrf()))
                .andExpect(flash().attributeExists("messages"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        course = courseRepository.findOne(course.getId());
        List<Category> categories = courseRepository.getCourseCategories(course);

        assertTrue(course.getName().equals(name)
                && course.getDescription().equals(description)
                && course.getStarts().equals(starts)
                && course.getExpires().equals(expires)
                && categories.contains(category)
        );
    }

    @Test
    public void testUserCanJoinCourse() throws Exception {
        Course course = createTestCourse("Join this");

        BDDMockito.given(accountServiceMock.getAuthenticatedUser()).willReturn(student);

        mockMvc.perform(post(URI + "/" + course.getId() + "/join")
                .with(user("student").roles("STUDENT"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        List<Account> students = courseRepository.getCourseStudents(course);
        assertTrue(students.contains(student));
    }

    @Test
    public void testUserCannotJoinMultipleTimesToSameCourse() throws Exception {
        Course course = createTestCourse("Join this");

        BDDMockito.given(accountServiceMock.getAuthenticatedUser()).willReturn(student);

        int courseStudentCountAtBeginning = courseRepository.getCourseStudents(course).size();

        mockMvc.perform(post(URI + "/" + course.getId() + "/join")
                .with(user("student").roles("STUDENT"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        mockMvc.perform(post(URI + "/" + course.getId() + "/join")
                .with(user("student").roles("STUDENT"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        List<Account> students = courseRepository.getCourseStudents(course);
        assertEquals(courseStudentCountAtBeginning + 1, students.size());
    }

    @Test
    public void testUserCanLeaveJoinedCourse() throws Exception {
        Course course = createTestCourse("Leave dis");
        courseService.addStudentToCourse(student, course);
        BDDMockito.given(accountServiceMock.getAuthenticatedUser()).willReturn(student);

        mockMvc.perform(post(URI + "/" + course.getId() + "/leave")
                .with(user("student").roles("STUDENT"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        List<Account> students = courseRepository.getCourseStudents(course);
        assertFalse(students.contains(student));
    }

    @Test
    public void testCategoryDetailIsDeletedWhenCourseIsDeleted() throws Exception {
        Category category = createTestCategory("Whee");
        Course course = new Course();
        course.setName("Deleting");
        course.setCourseCategories(Arrays.asList(category));
        course = courseRepository.save(course);

        CategoryDetail detail = categoryDetailsRepository.save(new CategoryDetail(course, category, LocalDate.MIN, LocalDate.MAX));
        int sizeBefore = categoryDetailsRepository.findAll().size();

        mockMvc.perform(delete(URI + "/" + course.getId() + "/delete")
                .with(user("teacher").roles("TEACHER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        assertEquals(sizeBefore - 1, categoryDetailsRepository.findAll().size());
    }
    
    @Test
    public void testCategoryIsAccessableThroughCourse() throws Exception {
        Category category = createTestCategory("Whee");
        Course course = new Course();
        course.setName("Here!");
        course.setCourseCategories(Arrays.asList(category));
        course = courseRepository.save(course);
        
        mockMvc.perform(get(URI + "/" + course.getId() + "/category/" + category.getId())
                .with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("course", "category"))
                .andReturn();
    }
    
    @Test
    public void testCategoryAccessedThroughCourseMustBelongToCourse() throws Exception {
        Category category = createTestCategory("Is not in the course");
        Course course = new Course();
        course.setName("No categories");
        course = courseRepository.save(course);
        
        mockMvc.perform(get(URI + "/" + course.getId() + "/category/" + category.getId())
                .with(user("student").roles("STUDENT")))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeDoesNotExist("category"))
                .andExpect(flash().attributeExists("messages"))
                .andReturn();
    }
    
    @Test
    public void testTaskIsAccessableThroughCourseAndCategory() throws Exception {
        Database database = createTestDatabase("Access");
        Task task = createTestTask("This place", database);
        Category category = createTestCategory("From here", Arrays.asList(task));
        Course course = new Course();
        course.setName("From hererer");
        course.setCourseCategories(Arrays.asList(category));
        courseRepository.save(course);
        
        mockMvc.perform(get(URI + "/" + course.getId() + "/category/" + category.getId() + "/task/" + task.getId())
                .with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("category", "course", "task"))
                .andReturn();
    }
    
    @Test
    public void testCategoryAccessedThroughCourseMustBelongToCourse2() throws Exception {
        Database database = createTestDatabase("Moonshine");
        Task task = createTestTask("In a side role", database);
        Category category = createTestCategory("Not in course", Arrays.asList(task));
        Course course = new Course();
        course.setName("No categories");
        courseRepository.save(course);
        
        mockMvc.perform(get(URI + "/" + course.getId() + "/category/" + category.getId() + "/task/" + task.getId())
                .with(user("student").roles("STUDENT")))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeDoesNotExist("task", "category"))
                .andExpect(flash().attributeExists("messages"))
                .andReturn();
    }
    
    @Test
    public void testTaskAccessedThroughCourseAndCategoryMustBelongToCourse() throws Exception {
        Database database = createTestDatabase("Must belong");
        Task task = createTestTask("Not in category", database);
        Category category = createTestCategory("From here", new ArrayList<>());
        Course course = new Course();
        course.setName("Not important");
        course.setCourseCategories(Arrays.asList(category));
        courseRepository.save(course);
        
        mockMvc.perform(get(URI + "/" + course.getId() + "/category/" + category.getId() + "/task/" + task.getId())
                .with(user("student").roles("STUDENT")))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeDoesNotExist("task"))
                .andExpect(flash().attributeExists("messages"))
                .andReturn();
    }
    
    @Test
    public void testQueryCanBeSentToTaskAccessedThroughCourseAndCategory() throws Exception{
        Database database = createTestDatabase("Pokemon master");
        Task task = createTestTask("Query to this", database);
        Category category = createTestCategory("From here", Arrays.asList(task));
        Course course = new Course();
        course.setName("From this");
        course.setCourseCategories(Arrays.asList(category));
        course.setStudents(Arrays.asList(student));
        course = courseRepository.save(course);
        
        //{courseId}/category/{categoryId}/task/{taskId}/query
        
        mockMvc.perform(post(URI + "/" + course.getId() + "/category/" + category.getId() + "/task/" + task.getId() + "/query")
                .param("query", "SELECT 1;")
                .with(user("student").roles("STUDENT")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("messages", "tables"))
                .andReturn();
    }
    
    @Test
    public void testQueryCanNotBeSentToTaskAccessedThroughCourseAndCategoryWhenNotJoinedToCourse() throws Exception {
        Database database = createTestDatabase("Pokemon master");
        Task task = createTestTask("Query to this", database);
        Category category = createTestCategory("From here", Arrays.asList(task));
        Course course = new Course();
        course.setName("From this");
        course.setCourseCategories(Arrays.asList(category));
        course = courseRepository.save(course);
        
        mockMvc.perform(post(URI + "/" + course.getId() + "/category/" + category.getId() + "/task/" + task.getId() + "/query")
                .param("query", "SELECT 1;")
                .with(user("student").roles("STUDENT")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", "You have not joined course " + course.getName()))
                .andReturn();
    }
}

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
import wepaht.SQLTasker.domain.LocalAccount;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.CategoryDetail;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;
import wepaht.SQLTasker.repository.LocalAccountRepository;
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
    LocalAccountRepository accountRepository;

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
    private TmcAccount student;
    private TmcAccount teacher;

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

        student = mock(TmcAccount.class);
        when(student.getUsername()).thenReturn("student");
        when(student.getRole()).thenReturn("STUDENT");
        when(student.getId()).thenReturn(43l);

        teacher = mock(TmcAccount.class);
        when(teacher.getUsername()).thenReturn("teacher");
        when(teacher.getRole()).thenReturn("TEACHER");
        when(teacher.getId()).thenReturn(44l);
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
    
    private Course createTestCourse(String name, TmcAccount user) {
        Course course = new Course();
        course.setName(name);
        course.setStudents(Arrays.asList(user));
        
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
    
    private Course createTestCourse(String name, Category category, LocalDate starts, LocalDate expires) {
        Course course = new Course();
        course.setName(name);
        course.setCourseCategories(Arrays.asList(category));
        course.setStarts(starts);
        course.setExpires(expires);
        course = courseRepository.save(course);
        return course;
    }
    
    private Course createTestCourseWithMultipleCategories(String name, List<Category> categories, LocalDate starts, LocalDate expires) {
        Course course = new Course();
        course.setName(name);
        course.setCourseCategories(categories);
        course.setStarts(starts);
        course.setExpires(expires);
        course = courseRepository.save(course);
        return course;
    }

    @Test
    public void testGetCoursesIsOk() throws Exception {
        when(accountServiceMock.getAuthenticatedUser()).thenReturn(student);
        mockMvc.perform(get(URI).with(user("student").roles("STUDENT")).with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testGetCoursesListsCourses() throws Exception {
        Course course1 = createTestCourse("listing courses1");
        Course course2 = createTestCourse("listing courses2");
        when(accountServiceMock.getAuthenticatedUser()).thenReturn(student);
        
        mockMvc.perform(get(URI)
                .with(user("student").roles("STUDENT"))
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

        List<TmcAccount> students = courseRepository.getCourseStudents(course);
        assertEquals(courseStudentCountAtBeginning + 1, students.size());
    }

    @Test
    public void testUserCanLeaveJoinedCourse() throws Exception {
        Course course = createTestCourse("Leave dis", student);        
        BDDMockito.given(accountServiceMock.getAuthenticatedUser()).willReturn(student);

        mockMvc.perform(post(URI + "/" + course.getId() + "/leave")
                .with(user("student").roles("STUDENT"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        List<TmcAccount> students = courseRepository.getCourseStudents(course);
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
    public void testQueryCanNotBeSentToTaskAccessedThroughCourseAndCategoryWhenNotJoinedToCourse() throws Exception {
        Database database = createTestDatabase("Pokemon master");
        Task task = createTestTask("Query to this", database);
        Category category = createTestCategory("From here", Arrays.asList(task));
        Course course = createTestCourse("From this", category, null, null);
        
        mockMvc.perform(post(URI + "/" + course.getId() + "/category/" + category.getId() + "/task/" + task.getId() + "/query")
                .param("query", "SELECT 1;")
                .with(user("student").roles("STUDENT")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", "You have not joined course " + course.getName()))
                .andReturn();
    }

    @Test
    public void testFeedbackCannotBeGivenToTaskWhenTaskIsNotDone() throws Exception {
        Task task = createTestTask("Feedback this", createTestDatabase("Durrr"));
        Category category = createTestCategory("In dis category", Arrays.asList(task));
        Course course = createTestCourse("Nerf this", category, null, null);
        
        mockMvc.perform(post(URI + "/" + course.getId() + "/category/" + category.getId() + "/task/" + task.getId() + "/feedback")
                .with(user("student").roles("STUDENT")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", 
                        Matchers.anyOf(Matchers.contains("You have not done task " + task.getName() + " in course " + course.getName()))));
    }
    
    @Test
    public void testStudentCanSeeOnlyActiveCourses() throws Exception {
        LocalDate now = LocalDate.now();
        Course course = createTestCourse("Active", null, now.minusDays(7l), now.plusDays(1l));
        Course course2 = createTestCourse("inactive", null, now.minusDays(7l), now.minusDays(1l));
        when(accountServiceMock.getAuthenticatedUser()).thenReturn(student);
        
        mockMvc.perform(get(URI).with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("courses", 
                        Matchers.anyOf(Matchers.hasSize(1))));
    }
    
    @Test
    public void testStudentCanOnlyAccessActiveCourses() throws Exception {
        LocalDate now = LocalDate.now();
        Course course = createTestCourse("inactive", null, now.minusDays(7l), now.minusDays(1l));
        
        mockMvc.perform(get(URI + "/" + course.getId())
                .with(user("student").roles("STUDENT")))
                .andExpect(status().is3xxRedirection());
    }
    
    @Test
    public void testStudentCanSeeOnlyActiveCategories() throws Exception {
        LocalDate now = LocalDate.now();
        Category category = createTestCategory("Active");
        Category category2 = createTestCategory("Inactive");
        Course course = createTestCourseWithMultipleCategories("The course", Arrays.asList(category, category2), null, null);
        CategoryDetail detail1 = createTestDetail(course, category, null, null);
        CategoryDetail detail2 = createTestDetail(course, category2, now.plusDays(1l), null);
        
        mockMvc.perform(get(URI + "/" + course.getId())
                .with(user("student").roles("STUDENT")))
                .andExpect(model().attribute("details", 
                        Matchers.anyOf(Matchers.hasSize(1))));
    }

    private CategoryDetail createTestDetail(Course course, Category category, LocalDate starts, LocalDate expires) {
        CategoryDetail detail = new CategoryDetail(course, category, starts, expires);
        return categoryDetailsRepository.save(detail);
    }
    
    @Test
    public void testStudentCannotAccessInactiveCourse() throws Exception {
        LocalDate now = LocalDate.now();
        Category category2 = createTestCategory("Inactive");
        Course course = createTestCourseWithMultipleCategories("The course", Arrays.asList(category2), null, null);
        CategoryDetail detail2 = createTestDetail(course, category2, now.plusDays(1l), null);
        
        mockMvc.perform(get(URI + "/" + course.getId() + "/category/" + detail2.getCategory().getId())
                .with(user("student").roles("STUDENT")))
                .andExpect(status().is3xxRedirection());
    }
}

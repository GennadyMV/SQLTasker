package wepaht.SQLTasker.controller;

import wepaht.SQLTasker.controller.CategoryController;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import wepaht.SQLTasker.Application;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.LocalAccount;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.TaskRepository;
import wepaht.SQLTasker.repository.LocalAccountRepository;
import wepaht.SQLTasker.service.DatabaseService;
import wepaht.SQLTasker.service.AccountService;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Assert;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.context.annotation.Profile;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.context.ActiveProfiles;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import wepaht.SQLTasker.domain.CategoryDetail;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.TmcAccount;
import wepaht.SQLTasker.constant.ConstantString;
import static wepaht.SQLTasker.constant.ConstantString.MESSAGE_SUCCESSFUL_ACTION;
import static wepaht.SQLTasker.constant.ConstantString.ROLE_ADMIN;
import static wepaht.SQLTasker.constant.ConstantString.ROLE_TEACHER;
import wepaht.SQLTasker.repository.CategoryDetailRepository;
import wepaht.SQLTasker.repository.CourseRepository;
import wepaht.SQLTasker.service.CourseService;
import wepaht.SQLTasker.service.TaskService;

@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class CategoryControllerTest {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private LocalAccountRepository userRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryDetailRepository categoryDetailRepository;

    @Mock
    AccountService userServiceMock;

    @InjectMocks
    CategoryController testingObject;

    private final String URI = "/categories";
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Database database;
    private MockMvc mockMvc;
    private TmcAccount user;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).apply(springSecurity()).build();

        databaseService.createDatabase("testDatabase4", "CREATE TABLE Persons"
                + "(PersonID int, LastName varchar(255), FirstName varchar(255), Address varchar(255), City varchar(255));"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (2, 'Raty', 'Matti', 'Rautalammintie', 'Helsinki');"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (1, 'Jaaskelainen', 'Timo', 'Jossakin', 'Heslinki');"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (3, 'Entieda', 'Kake?', 'Laiva', 'KJYR');");

        database = databaseService.getDatabaseByName("testDatabase4");
        
        user = mock(TmcAccount.class);
        when(user.getUsername()).thenReturn("stud");
        when(user.getRole()).thenReturn(ROLE_TEACHER);
        when(user.getId()).thenReturn(45l);
        when(userServiceMock.getAuthenticatedUser()).thenReturn(user);        
    }

    @After
    public void tearDown() {
        try {
            userRepository.deleteAll();
        } catch (Exception e) {
            System.out.println(e.toString());
        }        
        categoryDetailRepository.deleteAll();
        courseRepository.deleteAll();
        categoryRepository.deleteAll();
        taskRepository.findAllTaskIds().stream().forEach((taskId) -> {
            try {
                taskService.removeTask(taskId);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        });
    }

    private Task randomTask() {
        Task randomTask = new Task();
        randomTask.setName(RandomStringUtils.randomAlphanumeric(10));
        randomTask.setDescription(RandomStringUtils.randomAlphabetic(30));
        randomTask.setSolution("SELECT 1;");
        randomTask.setDatabase(database);
        return taskRepository.save(randomTask);
    }

    private Category createCategory() throws Exception {
        Category category = new Category();
        category.setName("create new category");
        category.setDescription("test");
        category.setTaskList(new ArrayList<>());

        return category;
    }

    @Test
    public void statusIsOk() throws Exception {
        mockMvc.perform(get(URI).with(user("stud").roles("STUDENT")))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void adminCanCreateNewCategory() throws Exception {
        Category category = createCategory();

        mockMvc.perform(post(URI)
                .param("name", category.getName())
                .param("description", category.getDescription())
                .param("starts", LocalDate.now().toString())
                .param("expires", LocalDate.MAX.toString())
                .with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", "Category has been created!"))
                .andReturn();

        List<Category> categoryList = categoryRepository.findAll();
        assertTrue(categoryList.stream().filter(cat -> cat.getName().equals("create new category")).findFirst().isPresent());
    }

    @Test
    public void adminCanCategorizeTasks() throws Exception {
        Category category = createCategory();
        category.setName("categorized tasks");
        Task task1 = randomTask();
        Task task2 = randomTask();
        Task task3 = randomTask();

        mockMvc.perform(post(URI)
                .param("name", category.getName())
                .param("description", category.getDescription())
                .param("taskIds", task1.getId().toString())
                .param("taskIds", task2.getId().toString())
                .param("taskIds", task3.getId().toString())
                .with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", "Category has been created!"))
                .andReturn();

        List<Category> categoryList = categoryRepository.findAll();
        Category created = categoryList.stream().filter(cat -> cat.getName().equals("categorized tasks")).findFirst().get();
        List<Task> tasks = created.getTaskList();

        assertTrue(tasks.containsAll(Arrays.asList(task1, task2, task3)));
    }

    @Test
    public void studentCannotSeeCategoriesOfWhichStartDateIsInFuture() throws Exception {
        Category futureCategory = createCategory();
        String name = "future";
        futureCategory.setName("future");

        MvcResult result = mockMvc.perform(get(URI).with(user("stud").roles("STUDENT")))
                .andExpect(model().attributeExists("categories"))
                .andExpect(status().isOk())
                .andReturn();

        List<Category> categories = (List) result.getModelAndView().getModel().get("categories");

        assertFalse(categories.stream().filter(cat -> cat.getName().equals(name)).findFirst().isPresent());
    }
}

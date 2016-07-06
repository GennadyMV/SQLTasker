package wepaht.SQLTasker.controller;

import java.util.ArrayList;
import wepaht.SQLTasker.domain.Tag;
import wepaht.SQLTasker.controller.TaskController;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.LocalAccount;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import wepaht.SQLTasker.Application;
import wepaht.SQLTasker.repository.DatabaseRepository;
import wepaht.SQLTasker.repository.TagRepository;
import wepaht.SQLTasker.repository.TaskRepository;
import java.util.List;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import wepaht.SQLTasker.repository.LocalAccountRepository;
import wepaht.SQLTasker.service.DatabaseService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.TmcAccount;
import static wepaht.SQLTasker.library.ConstantString.ROLE_STUDENT;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.TmcAccountRepository;
import wepaht.SQLTasker.service.PastQueryService;
import wepaht.SQLTasker.service.AccountService;

@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class TaskControllerTest {

    // /tasks/{categoryId}/{id}/query
    private final String API_URI = "/tasks";

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private PastQueryService pastQueryService;

    @Autowired
    private LocalAccountRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private TmcAccountRepository tmcAccountRepo;

    @Mock
    AccountService userServiceMock;

    @InjectMocks
    TaskController taskController;

    private MockMvc mockMvc = null;
    private Database database = null;
    private TmcAccount admin = null;
    private Category category = null;

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
        database = databaseRepository.findByNameAndDeletedFalse("testDatabase4").get(0);
        
        admin = mock(TmcAccount.class);
        when(admin.getUsername()).thenReturn("test");
        when(admin.getRole()).thenReturn("ADMIN");
        
        when(userServiceMock.getAuthenticatedUser()).thenReturn(admin);

        category = new Category();
        category.setName("taskCategory");
        category = categoryRepository.save(category);
    }

    @After
    public void tearDown() {
//        userRepository.deleteAll();
        try {
            categoryRepository.deleteAll();
        } catch (Exception e) {
        }

    }

    private Task randomTask() {
        Task task = new Task();
        task.setName(RandomStringUtils.randomAlphanumeric(10));
        task.setDescription(RandomStringUtils.randomAlphabetic(30));
        task.setDatabase(database);
        return task;
    }

    @Test
    public void statusIsOkTest() throws Exception {
        mockMvc.perform(get(API_URI).with(user("user").roles("TEACHER")))
                .andExpect(status().isOk());
    }

    @Test
    public void createSelectQuery() throws Exception {
        Task task = randomTask();
        task = taskRepository.save(task);

        String query = "select firstname, lastname from testdb";

        mockMvc.perform(post(API_URI + "/" + category.getId() + "/" + task.getId() + "/query").param("query", query).param("id", "" + task.getId()).with(user("test")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", Matchers.hasSize(1)))
                .andReturn();
    }

    @Test
    public void querysTableIsSeen() throws Exception {
        Task testTask = randomTask();
        testTask = taskRepository.save(testTask);

        mockMvc.perform(post(API_URI + "/" + category.getId() + "/" + testTask.getId() + "/query").param("query", "SELECT * FROM persons;").with(user("test")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("tables"))
                .andReturn();
    }

    @Test
    public void correctQueryIsAnnounced() throws Exception {
        Task testTask = randomTask();
        String solution = "SELECT firstname FROM persons;";
        testTask.setSolution(solution);
        testTask = taskRepository.save(testTask);

        mockMvc.perform(post(API_URI + "/" + category.getId() + "/" + testTask.getId() + "/query").param("query", solution).with(user("test")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", hasSize(2)))
                .andReturn();
    }

    @Test
    public void deleteTask() throws Exception {
        Task testTask = randomTask();
        taskRepository.save(testTask);
        assertNotNull(taskRepository.findOne(testTask.getId()));

        mockMvc.perform(delete(API_URI + "/" + testTask.getId()).with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", "Task deleted!"))
                .andReturn();

        assertNull(taskRepository.findOne(testTask.getId()));
    }

    @Test
    public void editTask() throws Exception {

        Task testTask = randomTask();
        taskRepository.save(testTask);
        assertNotNull(taskRepository.findOne(testTask.getId()));

        mockMvc.perform(post(API_URI + "/" + testTask.getId() + "/edit")
                .param("databaseId", "" + database.getId())
                .param("name", "Test")
                .param("solution", "SELECT * FROM persons;")
                .param("description", "It works")
                .with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", "Task modified!"))
                .andReturn();
        testTask = taskRepository.findOne(testTask.getId());
        assertEquals("Test", testTask.getName());
    }

    //Update-, delete-, drop-, insert- and create-queries use the same method
    @Test
    public void updateTypeQuery() throws Exception {
        Task testTask = randomTask();
        taskRepository.save(testTask);
        String sql = "UPDATE persons SET city='Helesinki' WHERE personid=3;";

        mockMvc.perform(post(API_URI + "/" + category.getId() + "/" + testTask.getId() + "/query")
                .param("query", sql)
                .with(user("test")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", hasSize(1)))
                .andReturn();
    }

    @Test
    public void teacherCanCreateTag() throws Exception {
        Task task = randomTask();
        task = taskRepository.save(task);
        String tagName = "cool tag bro";

        mockMvc.perform(post(API_URI + "/" + task.getId() + "/tags")
                .param("name", tagName)
                .with(user("teacher").roles("TEACHER")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", "Tag added!"))
                .andReturn();

        List<Tag> tags = tagRepository.findAll();

        assertTrue(tags.stream().filter(tag -> tag.getName().equals(tagName)).findFirst().isPresent());
    }

    @Test
    public void teacherCanDeleteTag() throws Exception {
        Task task = randomTask();
        task = taskRepository.save(task);
        Tag tag = new Tag();
        String tagName = "Diz iz ded";
        tag.setName(tagName);
        tag.setTask(task);
        tag = tagRepository.save(tag);

        mockMvc.perform(delete(API_URI + "/" + task.getId() + "/tags").param("name", tagName)
                .with(user("teacher").roles("TEACHER")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        List<Tag> tags = tagRepository.findAll();

        assertFalse(tags.stream().filter(t -> t.getName().equals(tagName)).findFirst().isPresent());
    }

    @Test
    public void createdTaskNameIsNotEmpty() throws Exception {
        String taskName = "";
        Long databaseId = database.getId();
        mockMvc.perform(post(API_URI).param("name", taskName)
                .param("description", "Name is not empty")
                .param("solution", "select * from persons;")
                .param("databaseId", databaseId.toString())
                .with(user("test").roles("ADMIN")).with(csrf()))
                //                .andExpect(status().is3xxRedirection())
                .andReturn();

        List<Task> tasks = taskRepository.findAll();

        assertFalse(tasks.stream().filter(task -> task.getName().equals(taskName)).findFirst().isPresent());
    }

    @Test
    public void createdTaskNameIsNotNull() throws Exception {
        Long databaseId = database.getId();

        String description = "Name is not null";

        mockMvc.perform(post(API_URI)
                .param("description", description)
                .param("solution", "select * from persons;")
                .param("databaseId", databaseId.toString())
                .with(user("test").roles("ADMIN")).with(csrf()))
                //                .andExpect(status().is3xxRedirection())
                .andReturn();

        List<Task> tasks = taskRepository.findAll();

        assertFalse(tasks.stream().filter(task -> task.getDescription().equals(description)).findFirst().isPresent());
    }
}

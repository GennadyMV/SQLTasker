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
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.TaskRepository;
import wepaht.SQLTasker.repository.AccountRepository;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.repository.CourseRepository;
import wepaht.SQLTasker.service.CourseService;
import wepaht.SQLTasker.service.TaskService;

@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
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
    private AccountRepository userRepository;

    @Autowired
    private TaskService taskService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private CourseRepository courseRepository;

    @Mock
    AccountService userServiceMock;

    @InjectMocks
    CategoryController testingObject;

    private final String URI = "/categories";
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Database database;
    private MockMvc mockMvc;
    private Account user;

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

        user = new Account();
        user.setUsername("stud");
        user.setPassword("test");
        user.setRole("STUDENT");
        user = userRepository.save(user);
        when(userServiceMock.getAuthenticatedUser()).thenReturn(user);
    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
        courseRepository.deleteAll();
        categoryRepository.deleteAll();
        taskRepository.findAllTaskIds().stream().forEach((taskId) -> {
            taskService.removeTask(taskId);
        });
    }

    private Task randomTask() {
        Task task = new Task();
        task.setName(RandomStringUtils.randomAlphanumeric(10));
        task.setDescription(RandomStringUtils.randomAlphabetic(30));
        task.setDatabase(database);
        return taskRepository.save(task);
    }

    private Category createCategory() throws Exception {
        Category category = new Category();
        category.setName("create new category");
        category.setDescription("test");
        category.setTaskList(new ArrayList<>());
        category.setStarts(LocalDate.of(0, 1, 1));
        category.setExpires(LocalDate.MAX);

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
    public void categoryCannotBeCreatedWihtoutAdmin() throws Exception {
        Category category = createCategory();
        category.setName("creation fails without permissions");

        mockMvc.perform(post(URI)
                .param("name", category.getName())
                .param("description", category.getDescription())
                .param("starts", category.getStarts().toString())
                .param("expires", category.getExpires().toString())
                .with(user("teacher").roles("TEACHER")).with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();

        List<Category> categoryList = categoryRepository.findAll();
        assertFalse(categoryList.stream().filter(cat -> cat.getName().equals("creation fails without permissions")).findFirst().isPresent());
    }

    @Test
    public void adminCanEditCreatedCategory() throws Exception {
        Category category = createCategory();
        category.setName("is editing possible?");
        category = categoryRepository.save(createCategory());

        mockMvc.perform(post(URI + "/" + category.getId() + "/edit")
                .param("name", "editing is possible")
                .param("description", category.getDescription())
                .param("startDate", category.getStarts().toString())
                .param("expiredDate", category.getExpires().toString())
                .with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", "Category modified!"))
                .andReturn();

        assertTrue(categoryRepository.findOne(category.getId()).getName().equals("editing is possible"));
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
                .param("starts", category.getStarts().toString())
                .param("expires", category.getExpires().toString())
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
        futureCategory.setStarts(futureCategory.getExpires());

        MvcResult result = mockMvc.perform(get(URI).with(user("stud").roles("STUDENT")))
                .andExpect(model().attributeExists("categories"))
                .andExpect(status().isOk())
                .andReturn();

        List<Category> categories = (List) result.getModelAndView().getModel().get("categories");

        assertFalse(categories.stream().filter(cat -> cat.getName().equals(name)).findFirst().isPresent());
    }

    @Test
    public void taskCanBeInMultipleCategories() throws Exception {
        Category category1 = createCategory();
        category1.setName("First Category");
        category1 = categoryRepository.save(category1);
        Category category2 = createCategory();
        category2.setName("Second Category");
        category2 = categoryRepository.save(category2);
        Task task = randomTask();

        mockMvc.perform(post(URI + "/" + category1.getId() + "/edit")
                .param("name", "First Category")
                .param("description", category1.getDescription())
                .param("startDate", category1.getStarts().toString())
                .param("expiredDate", category1.getExpires().toString())
                .param("taskIds", task.getId().toString())
                .with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", "Category modified!"))
                .andReturn();

        mockMvc.perform(post(URI + "/" + category2.getId() + "/edit")
                .param("name", "Second Category")
                .param("description", category2.getDescription())
                .param("startDate", category2.getStarts().toString())
                .param("expiredDate", category2.getExpires().toString())
                .param("taskIds", task.getId().toString())
                .with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", "Category modified!"))
                .andReturn();

        category1 = categoryRepository.findOne(category1.getId());
        category2 = categoryRepository.findOne(category2.getId());
        assertTrue(category1.getTaskList().contains(task) && category2.getTaskList().contains(task));
    }

    // categories/{id}/tasks/{taskId}
    @Test
    public void nextTaskExists() throws Exception {
        Task task1 = randomTask();
        Task task2 = randomTask();
        Category category = createCategory();
        category.getTaskList().add(task1);
        category.getTaskList().add(task2);
        category = categoryRepository.save(category);

        mockMvc.perform(get(URI + "/" + category.getId() + "/tasks/" + task1.getId())
                .with(user("stud").roles("STUDENT")).with(csrf()))
                .andExpect(model().attributeExists("next"))
                .andExpect(status().isOk())
                .andReturn();
    }
    
    @Test
    public void nextTaskIsNextTask() throws Exception {
        Task task1 = randomTask();
        Task task2 = randomTask();
        Category category = createCategory();
        category.getTaskList().add(task1);
        category.getTaskList().add(task2);
        category = categoryRepository.save(category);

        mockMvc.perform(get(URI + "/" + category.getId() + "/tasks/" + task1.getId())
                .with(user("stud").roles("STUDENT")).with(csrf()))
                .andExpect(model().attribute("next", task2))
                .andExpect(status().isOk())
                .andReturn();
    }
    
    @Test
    public void teacherCanReorderCategoryTasks()  throws Exception {
        user.setRole("TEACHER");
        userRepository.save(user);
        
        Task task1 = randomTask();
        Task task2 = randomTask();
        Category category = createCategory();
        category.getTaskList().add(task1);
        category.getTaskList().add(task2);
        category = categoryRepository.save(category);
        
        mockMvc.perform(post(URI + "/" + category.getId())
                .param("taskId", task1.getId().toString())
                .with(user("stud").roles("TEACHER")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        
        Assert.assertEquals(task2.getId(), categoryRepository.findOne(category.getId()).getTaskList().get(0).getId());
    }
    
    @Test
    public void testCategoryCanBeDeletedWhileInCourse() throws Exception {
        user.setRole("ADMIN");
        userRepository.save(user);
        
        Category category = createCategory();
        category = categoryRepository.save(category);
        String courseName = "Deleting course";
        courseService.createCourse(null, courseName, null, null, null, Arrays.asList(category.getId()));
        
        mockMvc.perform(delete(URI + "/" + category.getId())
                .with(user("stud").roles("ADMIN"))
                .with(csrf()))
                .andReturn();
        
        assertTrue(categoryRepository.findOne(category.getId()) == null);
    }
}

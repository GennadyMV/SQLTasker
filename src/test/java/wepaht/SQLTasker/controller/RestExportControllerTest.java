package wepaht.SQLTasker.controller;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import wepaht.SQLTasker.Application;
import wepaht.SQLTasker.domain.AuthenticationToken;
import wepaht.SQLTasker.domain.LocalAccount;
import wepaht.SQLTasker.repository.AuthenticationTokenRepository;
import wepaht.SQLTasker.repository.PastQueryRepository;
import wepaht.SQLTasker.repository.LocalAccountRepository;
import wepaht.SQLTasker.service.PastQueryService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.domain.Submission;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;
import static wepaht.SQLTasker.library.ConstantString.ROLE_ADMIN;
import static wepaht.SQLTasker.library.ConstantString.ROLE_STUDENT;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.DatabaseRepository;
import wepaht.SQLTasker.repository.SubmissionRepository;
import wepaht.SQLTasker.repository.TaskRepository;
import wepaht.SQLTasker.repository.TmcAccountRepository;
import wepaht.SQLTasker.service.AccountService;
import wepaht.SQLTasker.service.SubmissionService;
import wepaht.SQLTasker.service.TaskService;

@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class RestExportControllerTest {

    @Autowired
    TmcAccountRepository accountRepo;

    @Autowired
    AuthenticationTokenRepository tokenRepository;

    @Autowired
    PastQueryService pastQueryService;

    @Autowired
    PastQueryRepository pastQueryRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    DatabaseRepository dbRepo;
    
    @Autowired
    TaskService taskService;

    @Autowired
    SubmissionRepository submissionRepository;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Autowired
    private WebApplicationContext webAppContext;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc = null;
    private final String API_URI = "/export";
    private final String name1 = "0123456789";
    private final String name2 = "0987654321";
    private String authToken;
    private Task task1;
    private Task task2;
    private TmcAccount user;
    private TmcAccount stud1;
    private TmcAccount stud2;
    private Database db;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).apply(springSecurity()).build();

        pastQueryRepository.deleteAll();

        db = new Database();
        db.setName("Database");
        db.setDatabaseSchema("CREATE TABLE Persons"
                + "(PersonID int, LastName varchar(255), FirstName varchar(255), Address varchar(255), City varchar(255));"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (2, 'Raty', 'Matti', 'Rautalammintie', 'Helsinki');"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (1, 'Jaaskelainen', 'Timo', 'Jossakin', 'Heslinki');"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (3, 'Entieda', 'Kake?', 'Laiva', 'KJYR');");
        db = dbRepo.save(db);
        
        if (taskRepository.findByNameAndDeletedFalseOrderByNameDesc("Export test 1").isEmpty()) {
            task1 = new Task();
            task1.setName("Export test 1");
            task1.setDatabase(db);
            task1.setSolution("SELECT 1;");
            task1 = taskRepository.save(task1);
        } else {
            task1 = taskRepository.findByNameAndDeletedFalseOrderByNameDesc("Export test 1").get(0);
        }

        if (taskRepository.findByNameAndDeletedFalseOrderByNameDesc("Export test 2").isEmpty()) {
            task2 = new Task();
            task2.setName("Export test 2");
            task2.setDatabase(db);
            task2.setSolution("SELECT 1;");
            task2 = taskRepository.save(task2);            
        } else {
            task2 = taskRepository.findByNameAndDeletedFalseOrderByNameDesc("Export test 2").get(0);
        }

        user = accountRepo.findByUsernameAndDeletedFalse("admiini");
        if (user == null) {
            user = new TmcAccount();
            user.setUsername("admiini");
            user.setRole(ROLE_ADMIN);
            user = accountRepo.save(user);
        }

        stud1 = accountRepo.findByUsernameAndDeletedFalse(name1);
        if (stud1 == null) {
            stud1 = new TmcAccount();
            stud1.setUsername(name1);
            stud1.setRole(ROLE_STUDENT);
            stud1 = accountRepo.save(stud1);
        }

        stud2 = accountRepo.findByUsernameAndDeletedFalse(name2);
        if (stud2 == null) {
            stud2 = new TmcAccount();
            stud2.setUsername(name2);
            stud2.setRole(ROLE_STUDENT);
            stud2 = accountRepo.save(stud2);
        }

        submissionRepository.deleteAll();

        submissionRepository.save(new Submission(stud1, task1, null, null, "SELECT 1;", Boolean.TRUE));
        submissionRepository.save(new Submission(stud1, task2, null, null, "SELECT 1;", Boolean.TRUE));
        submissionRepository.save(new Submission(stud2, task1, null, null, "SELECT 1;", Boolean.TRUE));

        tokenRepository.deleteAll();
        AuthenticationToken token = new AuthenticationToken();
        token.setToken("");
        token.setUser(user);
        token = tokenRepository.save(token);
        authToken = token.getToken();

    }

    @After
    public void tearDown() {
        submissionRepository.deleteAll();
        pastQueryRepository.deleteAll();
        tokenRepository.deleteAll();
//        categoryRepository.deleteAll();
//        taskRepository.deleteAll();
    }

    @Test
    public void statusIsOk() throws Exception {
        mockMvc.perform(post(API_URI + "/points").param("exportToken", authToken)).andExpect(status().isOk()).andReturn();
    }

    @Test
    public void userCanGetPointsByUsername() throws Exception {
        mockMvc.perform(post(API_URI + "/points/" + name1).param("exportToken", authToken))
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.username", is(name1)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void userCanListAllPoints() throws Exception {
        mockMvc.perform(post(API_URI + "/points").param("exportToken", authToken))
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void pointsCanNotBeListedWithoutToken() throws Exception {
        mockMvc.perform(post(API_URI + "/points"))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}

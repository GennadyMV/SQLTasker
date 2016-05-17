package wepaht.controller;

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

//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import wepaht.Application;
import wepaht.domain.Database;
import wepaht.domain.Account;
import wepaht.repository.DatabaseRepository;
import wepaht.repository.PastQueryRepository;
import wepaht.repository.TaskRepository;

import wepaht.repository.UserRepository;
import wepaht.service.DatabaseService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import wepaht.domain.PastQuery;

import wepaht.service.PastQueryService;
import wepaht.service.UserService;

@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class PastQueryControllerTest {

    private final String API_URI = "/queries";

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
    private PastQueryRepository pastQueryRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    UserService userServiceMock;

    @InjectMocks
    PastQueryController testedObject;

    private MockMvc mockMvc = null;
    private PastQuery pastQuery = null;
    private Database database = null;
    private Account student = null;

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
        database = databaseRepository.findByName("testDatabase4").get(0);
        student = new Account();
        student.setUsername("stud");
        student.setPassword("test");
        student.setRole("STUDENT");
        student = userRepository.save(student);
        when(userServiceMock.getAuthenticatedUser()).thenReturn(student);
    }

    @After
    public void tearDown() {
        if (student != null) {
            userRepository.delete(student);
        }
    }

    @Test
    public void queryPageHasCorrectAttributes() throws Exception {

        mockMvc.perform(get(API_URI)
                .with(user("student").roles("STUDENT")).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("query"))
                .andExpect(model().attributeExists("queries","users"))
                .andReturn();
    }
    
    @Test
    public void findsQueryByTaskId() throws Exception {
        pastQueryService.saveNewPastQueryForTests("stud", 1337l, "select firstname from persons", true);
        
        mockMvc.perform(post(API_URI).param("taskId", ""+1337l).param("username", "allUsers").param("isCorrect", "allAnswers")
                    .with(user("teach").roles("TEACHER")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", "Here are queries:"))
                .andExpect(flash().attributeExists("pastQueries"))
                .andReturn();
    }
    
    @Test
    public void findsQueryByUsername() throws Exception {
        pastQueryService.saveNewPastQueryForTests("stud", 1337l, "select firstname from persons", true);
        
        mockMvc.perform(post(API_URI).param("username", "stud").param("isCorrect", "true")
                    .with(user("teach").roles("TEACHER")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", "Here are queries:"))
                .andExpect(flash().attributeExists("pastQueries"))
                .andReturn();    
    }
     
    @Test
    public void findsQueryByIscorrect() throws Exception {
        pastQueryService.saveNewPastQueryForTests("stud", 1337l, "select firstname from persons", true);
        
        mockMvc.perform(post(API_URI).param("username", "allUsers").param("isCorrect", "true")
                .with(user("teach").roles("TEACHER")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", "Here are queries:"))
                .andExpect(flash().attributeExists("pastQueries"))
                .andReturn();    }
    
    @Test 
    public void findsQueryByAllInfo() throws Exception { 
        pastQueryService.saveNewPastQueryForTests("stud", 1337l, "select firstname from persons", true);
        
        mockMvc.perform(post(API_URI).param("taskId", ""+1337l).param("username", "stud").param("isCorrect", "true")
                .with(user("teach").roles("TEACHER")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", "Here are queries:"))
                .andExpect(flash().attributeExists("pastQueries"))
                .andReturn();   
    }

    @Test
    public void studentFindsNoQuery() throws Exception {
        pastQueryRepository.deleteAll();
        mockMvc.perform(post(API_URI + "/student")
                .with(user("stud").roles("STUDENT")).with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("messages", "You have no past queries!"))
            .andExpect(flash().attributeExists("pastQueries"))
            .andReturn();
    }

    @Test
    public void studentFindsQuery() throws Exception {
        pastQueryService.saveNewPastQueryForTests("stud", 1337l, "select firstname from persons", true);

        mockMvc.perform(post(API_URI+"/student")
                    .param("username", "stud")
                    .with(user("stud").roles("STUDENT")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", "Here are your queries:"))
                .andExpect(flash().attributeExists("pastQueries"))
                .andReturn();
    }
}

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
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.repository.AuthenticationTokenRepository;
import wepaht.SQLTasker.repository.PastQueryRepository;
import wepaht.SQLTasker.repository.AccountRepository;
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
import wepaht.SQLTasker.domain.Submission;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.SubmissionRepository;
import wepaht.SQLTasker.repository.TaskRepository;
import wepaht.SQLTasker.service.AccountService;
import wepaht.SQLTasker.service.SubmissionService;
import wepaht.SQLTasker.service.TaskService;

@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class RestExportControllerTest {

    @Autowired
    AccountRepository userRepository;

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
    
    @Before
    public void setUp() {this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).apply(springSecurity()).build();

        pastQueryRepository.deleteAll();

        task1 = new Task();
        task1.setName("Export test 1");
        task1 = taskRepository.save(task1);
        task2 = new Task();
        task2.setName("Export test 2");
        task2 = taskRepository.save(task2);

        userRepository.deleteAll();
        Account user = new Account();
        user.setRole("ADMIN");
        user.setPassword("testi");
        user.setUsername("admiini");
        user = userRepository.save(user);
        
        Account stud1 = new Account();
        stud1.setRole("STUDENT");
        stud1.setPassword("testi");
        stud1.setUsername(name1);
        stud1 = userRepository.save(stud1);
        
        Account stud2 = new Account();
        stud2.setRole("STUDENT");
        stud2.setPassword("testi");
        stud2.setUsername(name2);
        stud2 = userRepository.save(stud2);
        
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
        categoryRepository.deleteAll();
        taskRepository.deleteAll();
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

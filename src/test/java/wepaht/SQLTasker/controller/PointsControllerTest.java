package wepaht.SQLTasker.controller;

import wepaht.SQLTasker.controller.PointsController;
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
import wepaht.SQLTasker.Application;
import wepaht.SQLTasker.repository.LocalAccountRepository;
import org.junit.After;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;
import wepaht.SQLTasker.repository.SubmissionRepository;
import wepaht.SQLTasker.repository.TaskRepository;
import wepaht.SQLTasker.service.PastQueryService;
import wepaht.SQLTasker.service.TaskService;
import wepaht.SQLTasker.service.AccountService;

@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class PointsControllerTest {

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private PastQueryService pastQueryService;

    @Autowired
    private LocalAccountRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;
    
    @Autowired
    private SubmissionRepository submissionRepository;

    @Mock
    AccountService userServiceMock;

    @InjectMocks
    PointsController testingObject;

    private MockMvc mockMvc = null;
    private TmcAccount teacher = null;
    private Task task;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).apply(springSecurity()).build();
        pastQueryService.deleteAllPastQueries();
        userRepository.deleteAll();
        teacher = mock(TmcAccount.class);
        when(teacher.getUsername()).thenReturn("user");
        when(teacher.getRole()).thenReturn("TEACHER");
        when(teacher.getId()).thenReturn(42l);
        when(userServiceMock.getAuthenticatedUser()).thenReturn(teacher);

        task = new Task();
        task.setName("Points test");
        task = taskRepository.save(task);
    }

    @After
    public void tearDown() {
        submissionRepository.deleteAll();
        try {
            taskService.removeTask(task.getId());
        } catch (Exception e) {
        }

    }

    @Test
    public void noPointslistWithoutPoints() throws Exception {
        mockMvc.perform(get("/points").with(user("user").roles("TEACHER")).with(csrf()))
                .andExpect(view().name("points"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("messages", "No points available."))
                .andReturn();
    }

    
}

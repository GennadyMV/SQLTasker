package wepaht.controller;

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
import wepaht.Application;
import wepaht.domain.Account;
import wepaht.repository.UserRepository;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import wepaht.service.PastQueryService;
import wepaht.service.UserService;

@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class PointsControllerTest {
    
    @Autowired
    private WebApplicationContext webAppContext;
    
    @Autowired
    private PastQueryService pastQueryService;

    @Autowired
    private UserRepository userRepository;

    @Mock
    UserService userServiceMock;

    @InjectMocks
    PointsController testingObject;

    private MockMvc mockMvc = null;
    private Account teacher = null;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).apply(springSecurity()).build();
        pastQueryService.deleteAllPastQueries();
        userRepository.deleteAll();
        teacher = new Account();
        teacher.setRole("TEACHER");
        teacher.setUsername("user");
        teacher.setPassword("test");
        teacher = userRepository.save(teacher);
        when(userServiceMock.getAuthenticatedUser()).thenReturn(teacher);
    }

    @Test
    public void noPointslistWithoutPoints() throws Exception {
        mockMvc.perform(get("/points").with(user("user").roles("TEACHER")).with(csrf()))
                .andExpect(view().name("points"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("messages", "No points available."))
                .andReturn();
    }
    
    @Test
    public void returnsTable() throws Exception {
        when(userServiceMock.getAuthenticatedUser()).thenReturn(teacher);
        for(Long l=0l; l<5; l++){
        pastQueryService.saveNewPastQueryForTests("student", l, "select firstname from persons", true);
        }
        mockMvc.perform(get("/points").with(user("user").roles("TEACHER")).with(csrf()))
                .andExpect(view().name("points"))
                .andExpect(model().attributeExists("tables"))
                .andExpect(status().isOk());
    }
}

package wepaht.SQLTasker.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import wepaht.SQLTasker.Application;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.repository.AccountRepository;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class AccountControllerTest {

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private AccountRepository userRepository;

    private MockMvc mockMvc = null;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).apply(springSecurity()).build();
    }

    @Test
    public void userCanRegister() throws Exception {
        mockMvc.perform(post("/register").param("username", "test1").param("password", "secret").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attribute("messages", "User created succesfully, please log in."))
                    .andReturn();

        Account testUser = userRepository.findByUsernameAndDeletedFalse("test1");

        assertTrue(testUser != null);
    }

    @Test
    public void accountIsByDefaultStudent() throws Exception {
        mockMvc.perform(post("/register").param("username", "test2").param("password", "secret").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("messages", "User created succesfully, please log in."))
                .andReturn();

        Account testUser = userRepository.findByUsernameAndDeletedFalse("test2");
        assertTrue(testUser.getRole().equals("STUDENT"));
    }
}

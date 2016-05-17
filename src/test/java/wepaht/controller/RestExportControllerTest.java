package wepaht.controller;

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
import wepaht.Application;
import wepaht.domain.AuthenticationToken;
import wepaht.domain.Account;
import wepaht.repository.AuthenticationTokenRepository;
import wepaht.repository.PastQueryRepository;
import wepaht.repository.UserRepository;
import wepaht.service.PastQueryService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class RestExportControllerTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationTokenRepository tokenRepository;

    @Autowired
    PastQueryService pastQueryService;

    @Autowired
    PastQueryRepository pastQueryRepository;

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

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).apply(springSecurity()).build();

        pastQueryRepository.deleteAll();

        pastQueryService.saveNewPastQueryForTests(name1, 1l, null, true);
        pastQueryService.saveNewPastQueryForTests(name1, 2l, null, true);
        pastQueryService.saveNewPastQueryForTests(name2, 1l, null, true);

        userRepository.deleteAll();
        Account user = new Account();
        user.setRole("ADMIN");
        user.setPassword("testi");
        user.setUsername("admiini");
        user = userRepository.save(user);

        tokenRepository.deleteAll();
        AuthenticationToken token = new AuthenticationToken();
        token.setToken("");
        token.setUser(user);
        token = tokenRepository.save(token);
        authToken = token.getToken();


    }

    @After
    public void tearDown() {
        tokenRepository.deleteAll();
    }

    @Test
    public void statusIsOk() throws Exception{
        mockMvc.perform(post(API_URI + "/points").param("exportToken", authToken)).andExpect(status().isOk()).andReturn();
    }

    @Test
    public void userCanGetPointsByUsername() throws Exception{
        mockMvc.perform(post(API_URI + "/points/" + name1).param("exportToken", authToken))
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.username", is(name1)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void userCanListAllPoints() throws Exception{
        mockMvc.perform(post(API_URI + "/points").param("exportToken", authToken))
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void pointsCanNotBeListedWithoutToken() throws Exception{
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

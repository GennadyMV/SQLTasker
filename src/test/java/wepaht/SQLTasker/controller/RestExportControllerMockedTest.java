package wepaht.SQLTasker.controller;

import java.nio.charset.Charset;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import wepaht.SQLTasker.Application;
import static wepaht.SQLTasker.constant.ConstantString.ROLE_ADMIN;
import wepaht.SQLTasker.domain.CustomExportToken;
import wepaht.SQLTasker.domain.TmcAccount;
import wepaht.SQLTasker.repository.CustomExportTokenRepository;

@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class RestExportControllerMockedTest {
    
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private final String API_URI = "/export";
    private CustomExportToken exportToken;
    private TmcAccount mockUser;
    
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    
    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }
    
    @Mock
    CustomExportTokenRepository tokenRepoMock;
    
    @InjectMocks
    RestExportController exportController;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mockUser = mock(TmcAccount.class);
        
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).apply(springSecurity()).build();
        
        exportToken = new CustomExportToken();
        exportToken.setToken("");
        exportToken.setUser(mockUser);
    }
    
    @Test
    public void statusIsForbiddenWithoutToken() throws Exception {
        when(tokenRepoMock.findByToken(any())).thenReturn(null);
        when(mockUser.getRole()).thenReturn(ROLE_ADMIN);
        
        mockMvc.perform(get(API_URI).param("exportToken", ""))
                .andExpect(status().isForbidden());
    }
    
    @Test
    public void statusIsOkWithToken() throws Exception { 
        when(tokenRepoMock.findByToken(any())).thenReturn(exportToken);
        when(mockUser.getRole()).thenReturn(ROLE_ADMIN);
        
        mockMvc.perform(get(API_URI).param("exportToken", exportToken.getToken())).andExpect(status().isOk()).andReturn();
    }
}

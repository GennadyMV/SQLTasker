package wepaht.SQLTasker.controller;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static org.hamcrest.Matchers.is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static wepaht.SQLTasker.constant.ConstantString.ROLE_ADMIN;
import wepaht.SQLTasker.domain.CustomExportToken;
import wepaht.SQLTasker.domain.TmcAccount;
import wepaht.SQLTasker.service.RestExportService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RestExportControllerMockedTest {
    
    @Mock
    RestExportService restService;
    
    @InjectMocks
    RestExportController controller;
    
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
    
    
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
 
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockUser = mock(TmcAccount.class);
        
        exportToken = new CustomExportToken();
        exportToken.setToken("");
        exportToken.setUser(mockUser);
    }
    
    @Test
    public void statusIsOkWithTokenToken() throws Exception {
        when(restService.getTokenByToken(exportToken.getToken())).thenReturn(exportToken);
        when(mockUser.getRole()).thenReturn(ROLE_ADMIN);
        
        mockMvc.perform(get(API_URI).param("exportToken", exportToken.getToken()))
                .andExpect(status().isOk());
    }
    
    @Test
    public void statusIsForbiddenWithoutTokenToken() throws Exception {
        when(restService.getTokenByToken(any())).thenReturn(null);
        when(mockUser.getRole()).thenReturn(ROLE_ADMIN);
        
        mockMvc.perform(get(API_URI).param("exportToken", ""))
                .andExpect(status().isForbidden());
    }
    
    @Test
    public void retrievePointsByCourseTest() throws Exception {
        String courseName = "kurssiOnKiva";
        String student1 = "0123456789";
        String student2 = "0987654321";
        int points1 = 9001;
        int points2 = 42;
        
        HashMap<String, Integer> points = new HashMap<>();
        points.put(student1, points1);
        points.put(student2, points2);
        Map<String, Map<String, Integer>> testResponse = new HashMap<>();
        testResponse.put(courseName, points);
        
        when(restService.getTokenByToken(exportToken.getToken())).thenReturn(exportToken);
//        when(restService.getCoursePoints(courseName)).thenReturn(testResponse);
        when(mockUser.getRole()).thenReturn(ROLE_ADMIN);
        
        
        MvcResult result = mockMvc.perform(get(API_URI + "/courses/" + courseName + "/points").param("exportToken", exportToken.getToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$." + courseName, is(testResponse)))
                .andReturn();      
    }
        
}

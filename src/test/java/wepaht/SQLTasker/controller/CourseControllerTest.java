package wepaht.SQLTasker.controller;

import java.time.LocalDate;
import org.hamcrest.Matchers;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
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
import wepaht.SQLTasker.service.UserService;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.CourseRepository;
import wepaht.SQLTasker.service.CourseService;

@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class CourseControllerTest {
    
    @Autowired
    CategoryRepository categoryRepository;
    
    @Autowired
    CourseRepository courseRepository;
    
    @Autowired
    CourseService courseService;
    
    @Autowired
    private WebApplicationContext webAppContext;
    
    @Mock
    UserService userServiceMock;
    
    @InjectMocks
    CourseController courseController;
    
    private MockMvc mockMvc;
    private String URI = "/courses";
    
    public CourseControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).apply(springSecurity()).build();
    }
    
    @After
    public void tearDown() {
        courseRepository.deleteAll();
        categoryRepository.deleteAll();
    }
    
    private Course createTestCourse(String name) {
        Course course = new Course();
        course.setName(name);
        
        return courseRepository.save(course);
    }
    
    private Category createTestCategory(String name) {
        Category category = new Category();
        category.setName(name);
        category.setStarts(LocalDate.MIN);
        category.setExpires(LocalDate.MAX);
        
        return categoryRepository.save(category);
    }
    
    @Test
    public void testGetCoursesIsOk() throws Exception {
        mockMvc.perform(get(URI).with(user("stud").roles("STUDENT")).with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }
    
    @Test
    public void testGetCoursesListsCourses() throws Exception {
        Course course1 = createTestCourse("listing courses1");
        Course course2 = createTestCourse("listing courses2");
        
        mockMvc.perform(get(URI)
                .with(user("stud").roles("STUDENT"))
                .with(csrf()))
                .andExpect(model().attribute("courses", hasSize(2)))
                .andExpect(model().attribute("courses", Matchers.hasItem(
                                        Matchers.allOf(Matchers.hasProperty("id", Matchers.is(course1.getId()))))))
                .andExpect(model().attribute("courses", Matchers.hasItem(
                                        Matchers.allOf(Matchers.hasProperty("id", Matchers.is(course2.getId()))))))
                .andReturn();
    }
    
    @Test
    public void testTeacherCanCreateCourse() throws Exception {
        String name = "create course";
        
        mockMvc.perform(post(URI).param("name", name)
                .with(user("teach").roles("TEACHER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        
        assertTrue(courseRepository.findAll().stream()
                .filter(course -> course.getName().equals(name))
                .findFirst().isPresent());
    }
    
    @Test
    public void testTeacherCanCreateCourseWithAllParameters() throws Exception {
        String name = "Create reel course";
        String description = "Stuffs happen";
        String starts = LocalDate.MIN.toString();
        String expires = LocalDate.MAX.toString();
        
        Category category = createTestCategory("test category");
        
        mockMvc.perform(post(URI)
                .param("name", name)
                .param("description", description)
                .param("starts", starts)
                .param("expires", expires)
                .param("categoryIds", category.getId().toString())
                .with(user("teacher").roles("TEACHER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        
        assertTrue(courseRepository.findAll().stream()
                .filter(course -> course.getName().equals(name))
                .findFirst().isPresent());
    }
}

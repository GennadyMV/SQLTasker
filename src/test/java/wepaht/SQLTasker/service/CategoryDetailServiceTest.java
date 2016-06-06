package wepaht.SQLTasker.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wepaht.SQLTasker.Application;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.CategoryDetail;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.repository.CategoryDetailRepository;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.CourseRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class CategoryDetailServiceTest {

    @Autowired
    private CategoryDetailRepository categoryDetailsRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CourseRepository courseRepository;
    
    private Course course;
    private Category category;

    @Autowired
    private CategoryDetailService categoryDetailsService;

    public CategoryDetailServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        category = new Category();
        category.setName("Durr");
        category = categoryRepository.save(category);
        
        course = new Course();
        course.setName("Hurr");
        course.setCourseCategories(Arrays.asList(category));
        course = courseRepository.save(course);
        
    }

    @After
    public void tearDown() {
        categoryDetailsRepository.deleteAll();        
        courseRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    /**
     * Test of saveCategoryDetailsList method, of class CategoryDetailService.
     */
    @Test
    public void testSaveCategoryDetailsList() {
        CategoryDetail detail1 = new CategoryDetail(course, category, LocalDate.MIN, LocalDate.MAX);

        int sizeBefore = categoryDetailsRepository.findAll().size();
        categoryDetailsService.saveCategoryDetailsList(Arrays.asList(detail1));

        assertEquals(sizeBefore + 1, categoryDetailsRepository.findAll().size());
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.SQLTasker.profile;

import java.time.LocalDate;
import java.util.*;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.LocalAccount;
import wepaht.SQLTasker.domain.CategoryDetail;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;
import static wepaht.SQLTasker.constant.ConstantString.ROLE_ADMIN;
import static wepaht.SQLTasker.constant.ConstantString.ROLE_STUDENT;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.LocalAccountRepository;
import wepaht.SQLTasker.repository.CategoryDetailRepository;
import wepaht.SQLTasker.repository.CourseRepository;
import wepaht.SQLTasker.repository.DatabaseRepository;
import wepaht.SQLTasker.repository.TaskRepository;
import wepaht.SQLTasker.repository.TmcAccountRepository;
import wepaht.SQLTasker.service.CategoryDetailService;
import wepaht.SQLTasker.service.CategoryService;
import wepaht.SQLTasker.service.CourseService;
import wepaht.SQLTasker.service.DatabaseService;

@Configuration
@Profile(value = {"dev", "default"})
public class DevProfile {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private TmcAccountRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CategoryDetailRepository detailRepository;

    @PostConstruct
    public void init() {

        databaseService.createDatabase("persons", "CREATE TABLE Persons"
                + "(PersonID int, LastName varchar(255), FirstName varchar(255), Address varchar(255), City varchar(255));"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (2, 'Raty', 'Matti', 'Rautalammintie', 'Helsinki');"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (1, 'Jaaskelainen', 'Timo', 'Jossakin', 'Heslinki');"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (3, 'Entieda', 'Kake?', 'Laiva', 'KJYR');");

        Category category = new Category();
        category.setName("first week");
        category.setDescription("easybeasy");
        category = categoryRepository.save(category);

        for (int i = 0; i < 10; i++) {
            Task task = randomTask();
            taskRepository.save(task);
            categoryService.setCategoryToTask(category.getId(), task);
        }
        
        Course course = new Course();
        course.setName("Test course");
        course.setDescription("Dis a test");
        course.setCourseCategories(Arrays.asList(category));
        course = courseRepository.save(course);
        
        CategoryDetail details = new CategoryDetail(course, category, LocalDate.MIN, LocalDate.MAX);
        detailRepository.save(details);
        
        TmcAccount student = new TmcAccount();
        student.setUsername("sqldummy");
        student.setRole(ROLE_STUDENT);
        student.setDeleted(false);
        
        TmcAccount admin = new TmcAccount();
        admin.setUsername("mcraty");
        admin.setRole(ROLE_ADMIN);
        admin.setDeleted(false);
        
        userRepository.save(student);
        userRepository.save(admin);
//        courseService.createCourse(null, "Test course", null, null, "Dis a test", Arrays.asList(category.getId()));
    }

    public Task randomTask() {
        Task task = new Task();
        task.setName(RandomStringUtils.randomAlphanumeric(10));
        task.setDescription("Test data description: " + RandomStringUtils.randomAlphabetic(30));
        task.setDatabase(databaseRepository.findAll().get(0));
        task.setSolution("select address from persons");
        return task;
    }
    
    
}

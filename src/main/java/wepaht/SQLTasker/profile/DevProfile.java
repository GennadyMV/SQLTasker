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
import wepaht.SQLTasker.domain.CategoryDetail;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;
import static wepaht.SQLTasker.constant.ConstantString.ROLE_ADMIN;
import static wepaht.SQLTasker.constant.ConstantString.ROLE_STUDENT;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.CategoryDetailRepository;
import wepaht.SQLTasker.repository.CourseRepository;
import wepaht.SQLTasker.repository.DatabaseRepository;
import wepaht.SQLTasker.repository.TaskRepository;
import wepaht.SQLTasker.repository.TmcAccountRepository;
import wepaht.SQLTasker.service.CategoryService;
import wepaht.SQLTasker.service.CourseService;
import wepaht.SQLTasker.service.DatabaseService;
import wepaht.SQLTasker.service.SampleCourseService;

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
    
    @Autowired
    private SampleCourseService sampleService;

    @PostConstruct
    public void init() {

        TmcAccount admin = new TmcAccount();
        admin.setUsername("sqldummy");
        admin.setRole(ROLE_ADMIN);
        admin.setDeleted(false);

        userRepository.save(admin);
//        courseService.createCourse(null, "Test course", null, null, "Dis a test", Arrays.asList(category.getId()));

        sampleService.initCourse();
    }

    private Category createCategory(String name, String desc) {
        Category category;
        category = new Category();
        category.setName(name);
        category.setDescription(desc);
        category = categoryRepository.save(category);
        return category;
    }

    private void createTask(String taskname, String desc, Database db, String solution, Category category) {
        Task task = new Task();
        task.setName(taskname);
        task.setDescription(desc);
        task.setDatabase(db);
        task.setSolution(solution);
        taskRepository.save(task);
        categoryService.setCategoryToTask(category.getId(), task);
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.SQLTasker.profile;

import java.time.LocalDate;
import java.util.Date;
import java.util.*;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.UserRepository;
import wepaht.SQLTasker.repository.DatabaseRepository;
import wepaht.SQLTasker.repository.TaskRepository;
import wepaht.SQLTasker.service.CategoryService;
import wepaht.SQLTasker.service.DatabaseService;

/**
 *
 * @author Kake
 */
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
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private CategoryService categoryService;

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
        category.setStartDate(LocalDate.now());
        category.setExpiredDate(LocalDate.MAX);
        categoryRepository.save(category);

        for (int i = 0; i < 10; i++) {
            Task task = randomTask();
            taskRepository.save(task);
            categoryService.setCategoryToTask(category.getId(), task);
        }        
        
        Account student = new Account();
        student.setUsername("0123456789");
        student.setPassword("opiskelija");
        student.setRole("STUDENT");

        Account teacher = new Account();
        teacher.setUsername("avihavai");
        teacher.setPassword("vihainen");
        teacher.setRole("ADMIN");

        Account assistant = new Account();
        assistant.setUsername("assistant");
        assistant.setPassword("iassist");
        assistant.setRole("TEACHER");

        userRepository.save(student);
        userRepository.save(teacher);
        userRepository.save(assistant);
    }

    public Task randomTask() {
        Task task = new Task();
        task.setName(RandomStringUtils.randomAlphanumeric(10));
        task.setDescription(RandomStringUtils.randomAlphabetic(30));
        task.setDatabase(databaseRepository.findAll().get(0));
        task.setSolution("select address from persons");
        task.setCategoryList(new ArrayList<>());
        return task;
    }
}

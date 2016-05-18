/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.SQLTasker.service;

import wepaht.SQLTasker.service.TaskResultService;
import wepaht.SQLTasker.service.DatabaseService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wepaht.SQLTasker.Application;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.repository.DatabaseRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class TaskResultServiceTest {

    @Autowired
    TaskResultService taskResultService;

    @Autowired
    private DatabaseService databaseService;
    
    @Autowired
    private DatabaseRepository dbRepo;

    private Task testTask;
    private String correctQuery;
    private Database testDb;

    public TaskResultServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        databaseService.createDatabase("testDatabase", "CREATE TABLE Wow(id integer);"
                + "INSERT INTO Wow (id) VALUES (7);");
        testDb = dbRepo.findByName("testDatabase").get(0);
        
        correctQuery = "SELECT * FROM Wow;";
        testTask = new Task();
        testTask.setDatabase(testDb);
        testTask.setSolution(correctQuery);
    }

    @After
    public void tearDown() {
        dbRepo.delete(testDb);
    }

    @Test
    public void testCorrectQueryReturnsTrue() {
        System.out.println("testCorrectQueryReturnsTrue");
        assertTrue(taskResultService.evaluateSubmittedQueryResult(testTask, correctQuery));
    }

    public void testIncorrectQueryReturnsFalse() {
        System.out.println("testIncorrectQueryReturnsFalse");
        
        String incorrectQuery = "SELECT 8;";
        
        assertFalse(taskResultService.evaluateSubmittedQueryResult(testTask, incorrectQuery));
    }
    
    public void testIncorrectQueryWithSameResultReturnsFalse() {
        System.out.println("testIncorrectQueryWithSameResultReturnsFalse");
        
        testTask.setSolution("SELECT COUNT(*) FROM Wow;");
        String incorrectQuery = "SELECT 1;";
        
        assertFalse(taskResultService.evaluateSubmittedQueryResult(testTask, incorrectQuery));
    }
}

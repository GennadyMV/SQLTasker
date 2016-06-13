/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.SQLTasker.service;

import java.util.HashMap;
import java.util.List;
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
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wepaht.SQLTasker.Application;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TaskFeedback;
import wepaht.SQLTasker.repository.TaskRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class TaskFeedbackServiceTest {
    
    @Autowired
    TaskFeedbackService feedbackService;
    
    @Autowired
    TaskRepository taskRepository;
    
    public TaskFeedbackServiceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    /**
     * Test of createFeedback method, of class TaskFeedbackService.
     */
    @Test
    public void testSaveFeedback() {
        TaskFeedback feedback = new TaskFeedback();
        HashMap<String, String> fb = new HashMap<>();
        fb.put("feedback", "Dis gud");
        feedback.setFeedback(fb);
        
        Task task = new Task();
        task.setName("hurr");
        task = taskRepository.save(task);
        
        feedback.setTask(task);
        
        feedbackService.saveFeedback(feedback);
        
        assertTrue(feedbackService.listAllFeedback().contains(feedback));
    }
    
}

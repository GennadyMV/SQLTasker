package wepaht.SQLTasker.service;

import wepaht.SQLTasker.service.PointService;
import wepaht.SQLTasker.service.PastQueryService;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wepaht.SQLTasker.Application;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.repository.TaskRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class PointServiceTest {
    
    @Autowired
    PointService pointService;
    
    @Autowired
    PastQueryService pastQueryService;
    
    @Autowired
    TaskRepository taskRepository;        
    
    @Test
    public void tableHasOneRow() throws Exception {
        Task task = new Task();
        
        task.setName("Test42");
        task = taskRepository.save(task);
        
        pastQueryService.saveNewPastQueryForTests("student", task, "select firstname from persons", true);
        pointService.getAllPoints();
        assertEquals(1, pointService.pointsTable.getRows().size());
    }
}

package wepaht.service;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wepaht.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class PointServiceTest {
    
    @Autowired
    PointService pointService;
    
    @Autowired
    PastQueryService pastQueryService;
    
    @Test
    public void tableHasOneRow() throws Exception {
        pastQueryService.saveNewPastQueryForTests("student", 0l, "select firstname from persons", true);
        pointService.getAllPoints();
        assertEquals(1, pointService.pointsTable.getRows().size());
    }
}

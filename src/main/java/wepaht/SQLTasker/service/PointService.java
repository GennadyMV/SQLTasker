package wepaht.SQLTasker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.domain.Table;

import java.util.*;
import javax.annotation.PostConstruct;

import wepaht.SQLTasker.domain.PastQuery;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.repository.PastQueryRepository;
import wepaht.SQLTasker.repository.TaskRepository;

@Service
public class PointService {

    @Autowired
    PastQueryRepository pastQueryRepository;

    @Autowired
    PastQueryService pastQueryService;

    @Autowired
    TaskRepository taskRepository;

    public Table pointsTable;

    public Integer getPointsByUsername(String username) {

        List<PastQuery> pastQueries = pastQueryRepository.findByCorrectnessAndUsernameAndCanGetPoint(true, username, true);
        if (pastQueries.isEmpty()) {
            return 0;
        }

        List<Long> tasksCompleted = new ArrayList<>();

        for (PastQuery query : pastQueries) {
            Long taskId = query.getTaskId();
            if (!tasksCompleted.contains(taskId)) {
                tasksCompleted.add(taskId);
            }
        }

        return tasksCompleted.size();
    }

    public Table getPointsAndExercisesByUsername(String username) {

        List<PastQuery> pastQueries = pastQueryService.returnQuery(username, null, "allAnswers");
        if (pastQueries.isEmpty()) {
            return new Table("empty");
        }

        pointsTable = new Table("points");
        List<String> columns = new ArrayList<>();
        columns.add("exercise");
        columns.add("points");
        pointsTable.setColumns(columns);
        pointsTable.setRows(new ArrayList<>());
        Map<Long, Boolean> tasksCompleted = new HashMap<>();

        for (PastQuery query : pastQueries) {

            Long taskId = query.getTaskId();

            if (query.getCorrectness() && query.getCanGetPoint()) {
                tasksCompleted.put(taskId, true);
            } else {
                tasksCompleted.put(taskId, false);
            }
        }
        for (Long taskId : tasksCompleted.keySet()) {
            List<String> row = new ArrayList<>();
            row.add(taskRepository.findOne(taskId).getName());
            if (tasksCompleted.get(taskId)) {
                row.add("" + 1);
            } else {
                row.add("" + 0);
            }
            pointsTable.getRows().add(row);
        }
        return pointsTable;
    }

    public Table getAllPoints() {

        List<PastQuery> pastQueries = pastQueryService.returnQuery("allUsers", null, "true");
        if (pastQueries.isEmpty()) {
            return new Table("empty");
        }

        pointsTable = new Table("points");
        List<String> columns = new ArrayList<>();
        columns.add("username");
        columns.add("points");
        pointsTable.setColumns(columns);
        pointsTable.setRows(new ArrayList<>());

        Map<String, List> userData = new HashMap<>();

        for (PastQuery query : pastQueries) {

            String username = query.getUsername();
            if (!userData.containsKey(username)) {
                List<Long> tasksCompleted = new ArrayList<>();
                userData.put(username, tasksCompleted);
            }
            List<Long> tasksCompleted = userData.get(username);
            Long taskId = query.getTaskId();

            if (!tasksCompleted.contains(taskId)) {
                tasksCompleted.add(taskId);
            }
        }

        Set<String> usernames = userData.keySet();
        for (String username : usernames) {
            List<String> row = new ArrayList<>();
            row.add(username);
            row.add("" + userData.get(username).size());
            pointsTable.getRows().add(row);
        }
        return pointsTable;
    }

}

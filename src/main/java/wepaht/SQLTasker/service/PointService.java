package wepaht.SQLTasker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wepaht.SQLTasker.domain.Table;

import java.util.*;
import wepaht.SQLTasker.domain.Account;

import wepaht.SQLTasker.domain.PastQuery;
import wepaht.SQLTasker.domain.PointHolder;
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
    
    @Autowired
    SubmissionService submissionService;
    
    @Autowired
    AccountService accountService;

    public Table pointsTable;

    public Integer getPointsByUsername(String username) {
        Account account = accountService.getAccountByUsername(username);
        return submissionService.getAccountPoints(account);
    }

    public Table getExercisesAndAwardedByUsername(String username) {
        pointsTable = new Table("points");
        List<String> columns = new ArrayList<>();
        columns.add("exercise");
        columns.add("points");
        pointsTable.setColumns(columns);
        pointsTable.setRows(submissionService.getAccountSubmissions(accountService.getAccountByUsername(username)));
        
        return pointsTable;
    }

    public Table getAllPoints() {
        pointsTable = new Table("points");
        List<String> columns = new ArrayList<>();
        columns.add("username");
        columns.add("points");
        pointsTable.setColumns(columns);
        pointsTable.setRows(pastQueryService.getPoints());
    
        return pointsTable;
    }

    public List<PointHolder> exportAllPoints() {
        return pastQueryRepository.exportAllPoints();
    }
}

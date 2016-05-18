/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.SQLTasker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.domain.Table;

import java.util.*;

import wepaht.SQLTasker.domain.Task;

@Service
public class TaskResultService {

    @Autowired
    private DatabaseService databaseService;

    public boolean evaluateSubmittedQueryResult(Task task, String query) {
        Database database = task.getDatabase();
        if (databaseService.isValidQuery(database, query)) {
            Map<String, Table> queryResult = databaseService.performQuery(database.getId(), query);
            Map<String, Table> correctResult = databaseService.performQuery(database.getId(), task.getSolution());

            boolean isCorrectColumnsInResult = compareColumns(queryResult, correctResult);
            boolean isCorrectRowsInResult = compareRows(queryResult, correctResult);

            return isCorrectColumnsInResult && isCorrectRowsInResult;
        }
        return false;
    }

    private boolean compareColumns(Map<String, Table> query, Map<String, Table> correctAnswer) {
        Set<String> correctTables = correctAnswer.keySet();

        if (query.size() != correctAnswer.size()) {
            return false;
        }

        for (String table : correctTables) {
            if (!query.keySet().contains(table)) {
                return false;
            }
            List<String> correctColumns = correctAnswer.get(table).getColumns();
            List<String> queryColumns = query.get(table).getColumns();

            if (correctColumns.size() != queryColumns.size()) {
                return false;
            }

            for (String column : correctColumns) {
                if (!queryColumns.contains(column)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean compareRows(Map<String, Table> query, Map<String, Table> correctAnswer) {
        Set<String> correctTables = correctAnswer.keySet();

        if (query.size() != correctAnswer.size()) {
            return false;
        }

        for (String table : correctTables) {
            if (!query.keySet().contains(table)) {
                return false;
            }
            List<List<String>> correctRows = correctAnswer.get(table).getRows();
            List<List<String>> queryRows = query.get(table).getRows();

            if (queryRows.size() != correctRows.size()) {
                return false;
            }

            for (int i = 0; i < correctRows.size(); i++) {
                for (String cell : correctRows.get(i)) {
                    if (!queryRows.get(i).contains(cell)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
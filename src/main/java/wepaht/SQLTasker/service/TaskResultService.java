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

    /**
     * Evaluates given query and gives feedback.
     *
     * @param task task to query from
     * @param query query to evaluate
     * @return Index 0 contains Boolean was query correct. Index 1 contains
     * List<String> feedback-messages on which tests failed.
     */
    public Boolean evaluateSubmittedQueryResultWithFeedback(Task task, String query, List<String> messages) {
        Boolean isCorrect;
        Database db = task.getDatabase();

        isCorrect = isValidQueryWithFeedback(db, query, messages);
        if (isCorrect) {
            Map<String, Table> queryResult = databaseService.performQuery(db.getId(), query);
            Map<String, Table> correctResult = databaseService.performQuery(db.getId(), task.getSolution());

            isCorrect = validateTablesWithFeedback(queryResult, correctResult, messages);
            if (isCorrect) {
                isCorrect = validateColumnsWithFeedback(queryResult, correctResult, messages);
                if (isCorrect) {
                    isCorrect = validateRowsWithFeedback(queryResult, correctResult, messages, task.getSolution());
                }
            }
        }

        return isCorrect;
    }

    private Boolean isValidQueryWithFeedback(Database db, String query, List<String> messages) {
        if (databaseService.isValidQuery(db, query)) {
            return true;
        }
        messages.add("Query is not valid!");
        return false;
    }

    private Boolean validateColumnsWithFeedback(Map<String, Table> queryResult, Map<String, Table> correctResult, List<String> messages) {
        Boolean isCorrect = validateCountColumns(queryResult, correctResult, messages);
        if (isCorrect) {
            isCorrect = validateColumnNames(queryResult, correctResult, messages);
        }
        return isCorrect;
    }

    private Boolean validateRowsWithFeedback(Map<String, Table> queryResult, Map<String, Table> correctResult, List<String> messages, String correctQuery) {
        return validateCountRows(queryResult, correctResult, messages) && validateCompareRows(queryResult, correctResult, messages, correctQuery);
    }

    private Boolean validateCountColumns(Map<String, Table> queryResult, Map<String, Table> correctResult, List<String> messages) {
        Boolean isCorrect = true;
        for (String tableName : correctResult.keySet()) {
            int correctCount = correctResult.get(tableName).getColumns().size();
            int queryCount = queryResult.get(tableName).getColumns().size();

            if (queryCount != correctCount) {
                columnCountError(tableName, correctCount, queryCount, messages);
                isCorrect = false;
            }
        }
        return isCorrect;
    }

    private void columnCountError(String tableName, int correctCount, int queryCount, List<String> messages) {
        StringBuilder sb = new StringBuilder();
        sb.append("Table ").append(tableName).append(" should have ").append(correctCount).append(" columns, ");
        sb.append("but query had ").append(queryCount).append(" columns");
        messages.add(sb.toString());
    }

    /**
     * Query's columns should be in same order as the correct answer's columns.
     *
     * @param queryResult
     * @param correctResult
     * @param messages
     * @return
     */
    private Boolean validateColumnNames(Map<String, Table> queryResult, Map<String, Table> correctResult, List<String> messages) {
        Boolean isCorrect = true;

        for (String tableName : correctResult.keySet()) {
            List<String> queryTableColumns = queryResult.get(tableName).getColumns();
            List<String> correctTableColumns = correctResult.get(tableName).getColumns();

            for (int i = 0; i < correctTableColumns.size(); i++) {
                if (!correctTableColumns.get(i).equals(queryTableColumns.get(i))) {
                    isCorrect = false;
                    columnNameError(tableName, i, correctTableColumns, queryTableColumns, messages);
                    break;
                }
            }
        }

        return isCorrect;
    }

    private void columnNameError(String tableName, int index, List<String> correctTableColumns, List<String> queryTableColumns, List<String> messages) {
        StringBuilder sb = new StringBuilder();
        sb.append("Table ").append(tableName).append(" column ").append(index + 1);
        sb.append(" should be ").append(correctTableColumns.get(index)).append(" but was ");
        sb.append(queryTableColumns.get(index));
        messages.add(sb.toString());
    }

    private Boolean validateCountRows(Map<String, Table> queryResult, Map<String, Table> correctResult, List<String> messages) {
        Boolean isCorrect = true;

        for (String tableName : correctResult.keySet()) {
            int correctRowCount = correctResult.get(tableName).getRows().size();
            int queryRowCount = queryResult.get(tableName).getRows().size();

            if (correctRowCount != queryRowCount) {
                isCorrect = false;
                rowCountError(tableName, correctRowCount, queryRowCount, messages);
            }
        }

        return isCorrect;
    }

    private void rowCountError(String tableName, int correctRowCount, int queryRowCount, List<String> messages) {
        StringBuilder sb = new StringBuilder();

        sb.append("Table ").append(tableName).append(" should have ");
        sb.append(correctRowCount).append(" rows, but query had ").append(queryRowCount);
        sb.append(" rows");

        messages.add(sb.toString());
    }

    private Boolean validateTablesWithFeedback(Map<String, Table> queryResult, Map<String, Table> correctResult, List<String> messages) {
        Boolean isCorrect = true;

        for (String tableName : correctResult.keySet()) {
            if (queryResult.get(tableName) == null) {
                isCorrect = false;
                StringBuilder sb = new StringBuilder("Query should have table ");
                sb.append(tableName);
                messages.add(sb.toString());
            }
        }

        return isCorrect;
    }

    private Boolean validateCompareRows(Map<String, Table> queryResult, Map<String, Table> correctResult, List<String> messages, String correctQuery) {
        Boolean isCorrect = true;
        if (correctQuery.toUpperCase().contains("ORDER BY")) {
            for (String tableName : correctResult.keySet()) {
                List<List<String>> correctRows = correctResult.get(tableName).getRows();
                List<List<String>> queryRows = queryResult.get(tableName).getRows();
                
                for (int i = 0; i < correctRows.size() && isCorrect == true; i++) {
                    isCorrect = compareRows(correctRows, i, queryRows, messages, tableName, isCorrect);
                }
            }
        }

        return isCorrect;
    }

    private Boolean compareRows(List<List<String>> correctRows, int i, List<List<String>> queryRows, List<String> messages, String tableName, Boolean isCorrect) {
        for (int j = 0; j < correctRows.get(i).size() && isCorrect == true; j++) {
            if (!correctRows.get(i).get(j).equals(queryRows.get(i).get(j))) {
                messages.add("Query table " + tableName + " row " + (i + 1) + " is incorrect.");
                isCorrect = false;                
            }
        }
        return isCorrect;
    }
}

package wepaht.SQLTasker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.domain.Table;
import wepaht.SQLTasker.repository.DatabaseRepository;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.*;
import wepaht.SQLTasker.domain.Account;

@Service
public class DatabaseService {

    @Autowired
    private DatabaseRepository databaseRepository;
    
    @Autowired
    private AccountService accountService;

    private HashSet<String> defaultTables = new HashSet<>();

    @PostConstruct
    private void init() {
        defaultTables.addAll(Arrays.asList(
                "CATALOGS",
                "COLLATIONS",
                "COLUMNS",
                "COLUMN_PRIVILEGES",
                "CONSTANTS",
                "CONSTRAINTS",
                "CROSS_REFERENCES",
                "DOMAINS",
                "FUNCTION_ALIASES",
                "FUNCTION_COLUMNS",
                "HELP",
                "INDEXES",
                "IN_DOUBT",
                "LOCKS",
                "QUERY_STATISTICS",
                "RIGHTS",
                "ROLES",
                "SCHEMATA",
                "SEQUENCES",
                "SESSIONS",
                "SESSION_STATE",
                "SETTINGS",
                "TABLES",
                "TABLE_PRIVILEGES",
                "TABLE_TYPES",
                "TRIGGERS",
                "TYPE_INFO",
                "USERS",
                "VIEWS"));
    }

    public boolean createDatabase(String name, String createTable) {
        try {
            Database db = new Database();
            Account user = null;
            try {
                user = accountService.getAuthenticatedUser();
            } catch (Exception e) {
                System.out.println("Account service is not initialized");
            }
            
            db.setName(name);
            db.setDatabaseSchema(createTable);
            db.setOwner(user);

            //Testing the connection
            Connection conn = createConnectionToDatabase(name, createTable);
            conn.close();

            databaseRepository.save(db);

            return true;
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * Lists tables of a single Database-entity. Example of use found in database.html-resource file and
     * DatabaseController.
     *
     * @param databaseId ID of selected database
     * @param updateQuery teehee
     * @return A map in which String-object indicates the name of certain table, and Table contains its' columns
     * and rows in separate lists. In case of broken database, the only returned table name is "ERROR".
     */
    public Map<String, Table> performUpdateQuery(Long databaseId, String updateQuery) {
        HashMap<String, Table> listedDatabase = new HashMap<>();
        Database database = databaseRepository.findOne(databaseId);
        Connection connection = null;
        String query = database.getDatabaseSchema();

        if (updateQuery != null) query += updateQuery;

        try {
            connection = createConnectionToDatabase(database.getName(), query);

            List<String> tables = listDatabaseTables(connection);

            final Connection finalConnection = connection;
            tables.parallelStream().forEach(tableName -> {
                Table table = new Table(tableName);
                try {
                    table.setColumns(listTableColumns(tableName, finalConnection));
                    table.setRows(listTableRows(tableName, table.getColumns(), finalConnection));
                } catch (Exception e) {
                }
                listedDatabase.put(tableName, table);
            });

            finalConnection.close();
        } catch (Exception e) {
            listedDatabase.put("ERROR", null);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }

        return listedDatabase;
    }

    /**
     * Performs a query in the selected database.
     *
     * @param databaseId ID of the selected database
     * @param sqlQuery   the query. Syntax must be correct in order this to work!
     * @return a table-object, which contains separately its' columns and rows. In case of sql error, returned table
     * will be named as the exception.
     */
    public Map<String, Table> performQuery(Long databaseId, String sqlQuery) {

        if (isUpdateQuery(sqlQuery)) {
            return performUpdateQuery(databaseId, sqlQuery);
        }

        Map<String, Table> queryResult = new HashMap<>();
        Table table = new Table("query");
        Database database = databaseRepository.findOne(databaseId);
        Statement statement = null;
        Connection connection = null;

        try {
            connection = createConnectionToDatabase(database.getName(), database.getDatabaseSchema());
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            table.setColumns(listQueryColumns(resultSet));
            table.setRows(listQueryRows(resultSet, table.getColumns()));
            queryResult.put("Query", table);
        } catch (Exception e) {
            queryResult.put(e.toString(), null);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }

        return queryResult;
    }

    public boolean isValidQuery(Database database, String sqlQuery) {
        Statement statement = null;
        Connection connection = null;
        Boolean isValid = true;

        try {
            if (isUpdateQuery(sqlQuery)) {
                connection = createConnectionToDatabase(database.getName(), database.getDatabaseSchema() + sqlQuery);
            } else {
                connection = createConnectionToDatabase(database.getName(), database.getDatabaseSchema());
                statement = connection.createStatement();
                statement.executeQuery(sqlQuery);
            }
        } catch (Exception e) {
            isValid = false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }

        return isValid;
    }

    private boolean isUpdateQuery(String sql) {
        sql = sql.toUpperCase();
        if (sql.contains("INSERT") || sql.contains("CREATE") || sql.contains("DROP") || sql.contains("UPDATE") || sql.contains("DELETE")) {
            return true;
        }

        return false;
    }

    private List<List<String>> listQueryRows(ResultSet resultSet, List<String> columns) throws Exception {
        List<List<String>> rows = new ArrayList<>();

        while (resultSet.next()) {
            List<String> row = new ArrayList<>();
            for (String column : columns) {
                row.add(resultSet.getString(column));
            }
            rows.add(row);
        }


        return rows;
    }

    private List<String> listQueryColumns(ResultSet resultSet) throws Exception {
        HashSet<String> columns = new HashSet<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int numberOfColumns = metaData.getColumnCount();

        for (int i = 1; i < numberOfColumns + 1; i++) {
            String columnName = metaData.getColumnName(i);
            columns.add(columnName);
        }

        return new ArrayList<String>(columns);
    }

    private List<String> listDatabaseTables(Connection connection) throws Exception {
        ArrayList<String> tables = new ArrayList<>();

        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(null, null, "%", null);

        while (resultSet.next()) {
            String tableName = resultSet.getString(3);

            if (!defaultTables.contains(tableName)) {
                tables.add(tableName);
            }
        }

        return tables;
    }

    private List<String> listTableColumns(String tableName, Connection connection) throws Exception {
        ArrayList<String> columns = new ArrayList<>();

        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getColumns(null, null, tableName, null);

        while (resultSet.next()) {
            String columnName = resultSet.getString(4);

            columns.add(columnName);
        }

        return columns;
    }

    private List<List<String>> listTableRows(String tableName, List<String> columns, Connection connection) throws Exception {
        List<List<String>> rows = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + tableName + ";";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(selectQuery);

        while (resultSet.next()) {
            ArrayList<String> row = new ArrayList<>();

            for (String column : columns) {
                row.add(resultSet.getString(column));
            }

            rows.add(row);
        }

        return rows;
    }

    private Connection createConnectionToDatabase(String databaseName, String sql) throws Exception {
        Class.forName("org.h2.Driver");
        String url = "jdbc:h2:mem:" + databaseName;
        String user = "lol";
        String pwds = "hah";

        Connection conn = DriverManager.getConnection(url, user, pwds);

        Statement statement = conn.createStatement();
        try {
            int result = statement.executeUpdate(sql);
        } catch (Exception e) {
            conn.close();
            return null;
        }
        

        return conn;
    }
}

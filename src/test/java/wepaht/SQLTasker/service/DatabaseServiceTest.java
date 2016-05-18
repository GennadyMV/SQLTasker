package wepaht.SQLTasker.service;

import wepaht.SQLTasker.service.DatabaseService;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wepaht.SQLTasker.Application;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.domain.Table;
import wepaht.SQLTasker.repository.DatabaseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class DatabaseServiceTest {

    @Autowired
    DatabaseService dbService;

    @Autowired
    DatabaseRepository dbRepository;

    Database biggerDatabase = null;

    @Before
    public void init() {
        dbService.createDatabase("testDatabase4", "CREATE TABLE Persons"
                + "(PersonID int, LastName varchar(255), FirstName varchar(255), Address varchar(255), City varchar(255));"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (2, 'Raty', 'Matti', 'Rautalammintie', 'Helsinki');"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (1, 'Jaaskelainen', 'Timo', 'Jossakin', 'Heslinki');"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (3, 'Entieda', 'Kake?', 'Laiva', 'KJYR');");
        biggerDatabase = dbRepository.findByName("testDatabase4").get(0);
    }

    @Test
    public void databaseCanBeCreatedWithCorrectCreateTablesTest() {
        String testDbName = "TestDatabase";
        String testDbCreateTable = "CREATE TABLE Test(id integer);" +
                                    "INSERT INTO Test (id) VALUES (7);";

        dbService.createDatabase(testDbName, testDbCreateTable);

        List<Database> databases = dbRepository.findByName(testDbName);

        assertTrue(databases.stream().filter(db -> db.getDatabaseSchema().equals(testDbCreateTable)).findFirst().isPresent());
    }

    @Test
    public void databaseCanNotBeCreatedWithIncorrectCreateTablesTest() {
        String testDbName = "TestDatabase2";
        String testDbIncorrectCreateTable = "Haha, lol";

        dbService.createDatabase(testDbName, testDbIncorrectCreateTable);

        List<Database> databases = dbRepository.findByName(testDbName);

        assertFalse(databases.stream().filter(db -> db.getDatabaseSchema().equals(testDbIncorrectCreateTable)).findFirst().isPresent());
    }

    @Test
    public void listedDatabasesContainsCreatedDatabases() throws Exception {
        String testDbName = "TestDatabase3";
        String tableName = "testing";
        String testDbCreateTable = "CREATE TABLE " + tableName +"(id integer);" +
                "INSERT INTO " + tableName + " (id) VALUES (7);";

        dbService.createDatabase(testDbName, testDbCreateTable);
        Database testDb = dbRepository.findByName(testDbName).get(0);

        Map<String, Table> table = dbService.performUpdateQuery(testDb.getId(), null);

        assertTrue(table.keySet().contains(tableName.toUpperCase()));
    }

    @Test
    public void listedDatabaseContainsCreatedTables() throws Exception {
        Map<String, Table> tables = dbService.performUpdateQuery(biggerDatabase.getId(), null);
        List<String> tableNames = new ArrayList<>(tables.keySet());
        Table theTable = tables.get(tableNames.get(0));

        assertTrue(tables.keySet().size() == 1 && theTable.getColumns().size() == 5 && theTable.getRows().size() == 3);
    }

    @Test
    public void insertQueryCanBePerformed() throws Exception {
        int personId = 10;
        String lastName = "Testilä";
        String firstName = "Teemu";
        String address = "Testaajankatu";
        String city = "Tapanila";

        String sql = "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY) " +
                "VALUES ('" + personId +"', '" + lastName + "', '" + firstName + "', '" + address + "', '" + city + "');";

        Map<String, Table> tables = dbService.performQuery(biggerDatabase.getId(), sql);
        Table personTable = tables.get(tables.keySet().toArray()[0]);

        assertTrue(personTable.getRows().size() == 4);
    }

    @Test
    public void deleteQueryCanBePerformed() throws Exception {
        String sql = "DELETE FROM Persons WHERE PersonID=1;";

        Map<String, Table> tables = dbService.performQuery(biggerDatabase.getId(), sql);
        Table personTable = tables.get(tables.keySet().toArray()[0]);

        assertTrue(personTable.getRows().size() == 2);
    }

    @Test
    public void createQueryCanBePerformed() {
        String sql = "CREATE TABLE Testing (ID int, Foo varchar(255));";

        Map<String, Table> tables = dbService.performQuery(biggerDatabase.getId(), sql);

        assertTrue(tables.keySet().size() == 2);
    }

    public void dropQueryCanBePerformed() {
        String sql = "DROP TABLE persons;";

        Map<String, Table> tables = dbService.performQuery(biggerDatabase.getId(), sql);

        assertTrue(tables.keySet().size() == 0);
    }

    public void updateQueryCanBePerformed() {
        String changedCell = "Helesinki";
        String sql = "UPDATE persons SET city='" + changedCell + "' WHERE personid=3;";

        Map<String, Table> tables = dbService.performQuery(biggerDatabase.getId(), sql);
        Table personsTable = tables.get(tables.keySet().toArray()[0]);

        Boolean isUpdated = false;
        for (List<String> row : personsTable.getRows()) if (row.contains(changedCell)) isUpdated = true;

        assertTrue(isUpdated);
    }
}

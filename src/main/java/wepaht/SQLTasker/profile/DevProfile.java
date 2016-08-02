/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.SQLTasker.profile;

import java.time.LocalDate;
import java.util.*;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import wepaht.SQLTasker.domain.Category;
import wepaht.SQLTasker.domain.CategoryDetail;
import wepaht.SQLTasker.domain.Course;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.domain.TmcAccount;
import static wepaht.SQLTasker.constant.ConstantString.ROLE_ADMIN;
import static wepaht.SQLTasker.constant.ConstantString.ROLE_STUDENT;
import wepaht.SQLTasker.domain.Database;
import wepaht.SQLTasker.repository.CategoryRepository;
import wepaht.SQLTasker.repository.CategoryDetailRepository;
import wepaht.SQLTasker.repository.CourseRepository;
import wepaht.SQLTasker.repository.DatabaseRepository;
import wepaht.SQLTasker.repository.TaskRepository;
import wepaht.SQLTasker.repository.TmcAccountRepository;
import wepaht.SQLTasker.service.CategoryService;
import wepaht.SQLTasker.service.CourseService;
import wepaht.SQLTasker.service.DatabaseService;

@Configuration
@Profile(value = {"dev", "default"})
public class DevProfile {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private TmcAccountRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryDetailRepository detailRepository;

    @PostConstruct
    public void init() {

        databaseService.createDatabase("persons", "CREATE TABLE Persons"
                + "(PersonID int, LastName varchar(255), FirstName varchar(255), Address varchar(255), City varchar(255));"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (2, 'Raty', 'Matti', 'Rautalammintie', 'Helsinki');"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (1, 'Jaaskelainen', 'Timo', 'Jossakin', 'Heslinki');"
                + "INSERT INTO PERSONS (PERSONID, LASTNAME, FIRSTNAME, ADDRESS, CITY)"
                + "VALUES (3, 'Entieda', 'Kake?', 'Laiva', 'KJYR');");

        Category category = new Category();
        category.setName("first week");
        category.setDescription("easybeasy");
        category = categoryRepository.save(category);

        for (int i = 0; i < 10; i++) {
            Task task = randomTask();
            taskRepository.save(task);
            categoryService.setCategoryToTask(category.getId(), task);
        }

        Course course = new Course();
        course.setName("Test course");
        course.setDescription("Dis a test");
        course.setCourseCategories(Arrays.asList(category));
        course = courseRepository.save(course);

        CategoryDetail details = new CategoryDetail(course, category, LocalDate.MIN, LocalDate.MAX);
        detailRepository.save(details);

        TmcAccount student = new TmcAccount();
        student.setUsername("sqldummy");
        student.setRole(ROLE_STUDENT);
        student.setDeleted(false);

        TmcAccount admin = new TmcAccount();
        admin.setUsername("mcraty");
        admin.setRole(ROLE_ADMIN);
        admin.setDeleted(false);

        userRepository.save(student);
        userRepository.save(admin);
//        courseService.createCourse(null, "Test course", null, null, "Dis a test", Arrays.asList(category.getId()));

        //Ensimmäinen kategoria
        StringBuilder createTable = new StringBuilder();
        String opiskelija = "CREATE TABLE Opiskelija \n"
                + "(opiskelijanumero VARCHAR(9) PRIMARY KEY, nimi VARCHAR(255), syntymävuosi INTEGER(4), pääaine VARCHAR(255));\n"
                + "INSERT INTO Opiskelija (opiskelijanumero, nimi, syntymävuosi, pääaine) VALUES ('999999999', 'Matti', 1993, 'Tietojenkäsittelytiede');\n"
                + "INSERT INTO Opiskelija (opiskelijanumero, nimi, syntymävuosi, pääaine) VALUES ('999999995', 'Eero', 1993, 'Tietojenkäsittelytiede');\n"
                + "INSERT INTO Opiskelija (opiskelijanumero, nimi, syntymävuosi, pääaine) VALUES ('999999998', 'Kasper', 1992, 'Matematiikka');\n"
                + "INSERT INTO Opiskelija (opiskelijanumero, nimi, syntymävuosi, pääaine) VALUES ('999999997', 'Iida', 1993, 'Fysiikka');\n"
                + "INSERT INTO Opiskelija (opiskelijanumero, nimi, syntymävuosi, pääaine) VALUES ('999999996', 'Leo', 1994, 'Tietojenkäsittelytiede');";
        String kurssisuoritus1 = "CREATE TABLE Kurssisuoritus\n"
                + "(opiskelijanumero VARCHAR,\n"
                + "kurssi VARCHAR,\n"
                + "päivämäärä DATE,\n"
                + "arvosana INT(1),\n"
                + "FOREIGN KEY(opiskelijanumero) REFERENCES Opiskelija);\n"
                + "INSERT INTO Kurssisuoritus (opiskelijanumero, kurssi, päivämäärä, arvosana)\n"
                + "VALUES ('999999998', 'Ohjelmoinnin perusteet', '2014-10-1', 3);\n"
                + "INSERT INTO Kurssisuoritus (opiskelijanumero, kurssi, päivämäärä, arvosana)\n"
                + "VALUES ('999999998', 'Ohjelmoinnin jatkokurssi', '2015-01-1', 5);\n"
                + "INSERT INTO Kurssisuoritus (opiskelijanumero, kurssi, päivämäärä, arvosana)\n"
                + "VALUES ('999999999', 'Ohjelmoinnin jatkokurssi', '2015-01-1', 2);\n"
                + "INSERT INTO Kurssisuoritus (opiskelijanumero, kurssi, päivämäärä, arvosana)\n"
                + "VALUES ('999999995', 'Ohjelmoinnin perusteet', '2013-10-1', 1);\n"
                + "INSERT INTO Kurssisuoritus (opiskelijanumero, kurssi, päivämäärä, arvosana)\n"
                + "VALUES ('999999995', 'Tietokantojen perusteet', '2014-01-1', 5);\n"
                + "INSERT INTO Kurssisuoritus (opiskelijanumero, kurssi, päivämäärä, arvosana)\n"
                + "VALUES ('999999995', 'Ohjelmoinnin jatkokurssi', '2014-06-1', 5);\n"
                + "INSERT INTO Kurssisuoritus (opiskelijanumero, kurssi, päivämäärä, arvosana)\n"
                + "VALUES ('999999997', 'Tietokantojen perusteet', '2016-06-1', 4);\n"
                + "INSERT INTO Kurssisuoritus (opiskelijanumero, kurssi, päivämäärä, arvosana)\n"
                + "VALUES ('999999999', 'Ohjelmoinnin perusteet', '2016-10-1', 4);";
        createTable.append(opiskelija).append(kurssisuoritus1);
        databaseService.createDatabase("Yliopisto1", createTable.toString());

        Category category1 = createCategory("SQL-kyselykieli", "Tehtäväsarja esittelee SQL-kyselykielen perusominaisuuksia ja -toimintoja.");

        createTask("SELECT-kysely 1", "Valitse kaikki opiskelijat taulusta Opiskelija.", databaseRepository.findByNameAndDeletedFalse("Yliopisto1").get(0), "SELECT * FROM Opiskelija;", category1);
        createTask("SELECT-kysely 2", "Valitse kaikki kurssisuoritukset taulusta Kurssisuoritukset.", databaseRepository.findByNameAndDeletedFalse("Yliopisto1").get(0), "SELECT * FROM Kurssisuoritus;", category1);
        createTask("Sarakkeiden valitseminen", "Listaa kaikkien opiskelijoiden nimi ja syntymävuosi.", databaseRepository.findByNameAndDeletedFalse("Yliopisto1").get(0), "SELECT nimi, syntymävuosi FROM Opiskelija;", category1);
        createTask("Yksilölliset rivit", "Listaa kaikki erilaiset pääaineet.", databaseRepository.findByNameAndDeletedFalse("Yliopisto1").get(0), "SELECT DISTINCT pääaine FROM Opiskelija;", category1);
        createTask("Rivien rajaaminen", "Etsi vuonna -93 syntyneet opiskelijat.", databaseRepository.findByNameAndDeletedFalse("Yliopisto1").get(0), "SELECT * FROM Opiskelija WHERE syntymävuosi=1993;", category1);
        createTask("Merkkijonojen haku", "Etsi opiskelijat joiden nimissä on o-kirjain", databaseRepository.findByNameAndDeletedFalse("Yliopisto1").get(0), "SELECT * FROM Opiskelija WHERE nimi LIKE '%o%';", category1);
        createTask("Useampi hakuperuste 1", "Hae ohjelmoinnin jatkokurssin suoritteet, joiden arvosana on suurempi kuin 3",
                databaseRepository.findByNameAndDeletedFalse("Yliopisto1").get(0),
                "SELECT * FROM Kurssisuoritus WHERE kurssi LIKE '%Ohjelmoinnin jatkokurssi%' AND arvosana > 3;", category1);
        createTask("Useampi hakuperuste 2", "Hae ohjelmoinnin jatko- ja peruskurssin suoritteet",
                databaseRepository.findByNameAndDeletedFalse("Yliopisto1").get(0),
                "SELECT * FROM Kurssisuoritus WHERE kurssi LIKE '%Ohjelmoinnin jatkokurssi%' OR kurssi LIKE '%Ohjelmoinnin perusteet%';", category1);

        //Toinen kategoria
        String kurssi = "CREATE TABLE Kurssi\n"
                + "(kurssitunnus VARCHAR(6),\n"
                + "nimi VARCHAR,\n"
                + "kuvaus VARCHAR,\n"
                + "PRIMARY KEY(kurssitunnus));";
        createTable = new StringBuilder();
        createTable.append(opiskelija).append(kurssi);
        databaseService.createDatabase("Yliopisto2", createTable.toString());

        Category category2 = createCategory("Erilaiset yhteystyypit", "Käydään läpi tietokannan erilaiset yhteystyypit ja tutustutaan eri taulujen sisällön yhdistämiseen");

        course = new Course();
        course.setName("Tietokantojen perusteet - 0000");
        course.setDescription("Käyttövalmis esimerkkikurssi. Sovellus käyttää SQL:n murretta hsql. Hsql:n syntaksiohjeet löytyvät osoitteesta: http://www.hsqldb.org/doc/guide/ch09.html");
        course.setCourseCategories(Arrays.asList(category1, category2));
        course = courseRepository.save(course);

        details = new CategoryDetail(course, category1, null, null);
        detailRepository.save(details);
        details = new CategoryDetail(course, category2, null, null);
        detailRepository.save(details);
    }

    private Category createCategory(String name, String desc) {
        Category category;
        category = new Category();
        category.setName(name);
        category.setDescription(desc);
        category = categoryRepository.save(category);
        return category;
    }

    private void createTask(String taskname, String desc, Database db, String solution, Category category) {
        Task task = new Task();
        task.setName(taskname);
        task.setDescription(desc);
        task.setDatabase(db);
        task.setSolution(solution);
        taskRepository.save(task);
        categoryService.setCategoryToTask(category.getId(), task);
    }

    public Task randomTask() {
        Task task = new Task();
        task.setName(RandomStringUtils.randomAlphanumeric(10));
        task.setDescription("Test data description: " + RandomStringUtils.randomAlphabetic(30));
        task.setDatabase(databaseRepository.findAll().get(0));
        task.setSolution("select address from persons");
        return task;
    }

}

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

        TmcAccount admin = new TmcAccount();
        admin.setUsername("sqldummy");
        admin.setRole(ROLE_ADMIN);
        admin.setDeleted(false);

        userRepository.save(admin);
//        courseService.createCourse(null, "Test course", null, null, "Dis a test", Arrays.asList(category.getId()));

        //Ensimmäinen kategoria
        StringBuilder createTable = new StringBuilder();
        String opiskelija = "CREATE TABLE Opiskelija \n"
                + "(opiskelijanumero VARCHAR(9) NOT NULL PRIMARY KEY, nimi VARCHAR(255) NOT NULL, syntymävuosi INTEGER(4), pääaine VARCHAR(255), UNIQUE (opiskelijanumero));\n"
                + "INSERT INTO Opiskelija (opiskelijanumero, nimi, syntymävuosi, pääaine) VALUES ('999999999', 'Matti', 1731, 'Tietojenkäsittelytiede');\n"
                + "INSERT INTO Opiskelija (opiskelijanumero, nimi, syntymävuosi, pääaine) VALUES ('999999995', 'Eero', 1989, 'Tietojenkäsittelytiede');\n"
                + "INSERT INTO Opiskelija (opiskelijanumero, nimi, syntymävuosi, pääaine) VALUES ('999999998', 'Kasper', 1992, 'Matematiikka');\n"
                + "INSERT INTO Opiskelija (opiskelijanumero, nimi, syntymävuosi, pääaine) VALUES ('999999997', 'Iida', 1993, 'Kauppatieteet');\n"
                + "INSERT INTO Opiskelija (opiskelijanumero, nimi, syntymävuosi, pääaine) VALUES ('999999996', 'Leo', 1994, 'Tietojenkäsittelytiede');\n"
                + "INSERT INTO Opiskelija (opiskelijanumero, nimi, syntymävuosi, pääaine) VALUES ('999999993', 'Vampyyri', 1403, 'Matematiikka');\n"
                + "INSERT INTO Opiskelija (opiskelijanumero, nimi, syntymävuosi, pääaine) VALUES ('999999994', 'Arto', 1981l, 'Tietojenkäsittelytiede');\n";
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
        String kurssi = "\nCREATE TABLE Kurssi\n"
                + "(kurssitunnus VARCHAR(7),\n"
                + "nimi VARCHAR,\n"
                + "kuvaus VARCHAR,\n"
                + "PRIMARY KEY(kurssitunnus));\n"
                + "INSERT INTO Kurssi\n"
                + "(kurssitunnus, nimi, kuvaus)\n"
                + "VALUES ('581328', 'Tietokantojen perusteet', 'Kurssilla tutustutaan tiedon esitysmuotoihin ja tiedon hakuun suurista tietomääristä.');\n"
                + "INSERT INTO Kurssi\n"
                + "(kurssitunnus, nimi, kuvaus)\n"
                + "VALUES ('581325', 'Ohjelmoinnin perusteet', 'Kurssilla perehdytään nykyaikaisen ohjelmoinnin perusideoihin sekä algoritmien laatimiseen.');\n"
                + "INSERT INTO Kurssi\n"
                + "VALUES ('582102', 'Johdatus tietojenkäsittelytieteeseen', 'Opintojaksolla tutustutaan tietojenkäsittelyn keskeisiin osa-alueisiin, menetelmiin ja ammattietiikkaan.');\n"
                + "INSERT INTO Kurssi\n"
                + "(kurssitunnus, nimi, kuvaus)\n"
                + "VALUES ('582103', 'Ohjelmoinnin jatkokurssi', 'Kurssilla perehdytään olio-ohjelmoinnin perustekniikoihin.');\n";

        String kurssisuoritus2 = "\nCREATE TABLE Kurssisuoritus\n"
                + "(opiskelija VARCHAR,\n"
                + "kurssi VARCHAR,\n"
                + "päivämäärä DATE,\n"
                + "arvosana INT(1),\n"
                + "FOREIGN KEY(opiskelija) REFERENCES Opiskelija (opiskelijanumero),\n"
                + "FOREIGN KEY(kurssi) REFERENCES Kurssi (kurssitunnus));\n"
                + "INSERT INTO Kurssisuoritus (opiskelija, kurssi, päivämäärä, arvosana)\n"
                + "VALUES ('999999998', '581325', '2014-10-1', 3);\n"
                + "INSERT INTO Kurssisuoritus (opiskelija, kurssi, päivämäärä, arvosana)\n"
                + "VALUES ('999999998', '582103', '2015-01-1', 5);\n"
                + "INSERT INTO Kurssisuoritus (opiskelija, kurssi, päivämäärä, arvosana)\n"
                + "VALUES ('999999999', '582103', '2015-01-1', 2);\n"
                + "INSERT INTO Kurssisuoritus (opiskelija, kurssi, päivämäärä, arvosana)\n"
                + "VALUES ('999999995', '581325', '2013-10-1', 1);\n"
                + "INSERT INTO Kurssisuoritus (opiskelija, kurssi, päivämäärä, arvosana)\n"
                + "VALUES ('999999995', '581328', '2014-01-1', 5);\n"
                + "INSERT INTO Kurssisuoritus (opiskelija, kurssi, päivämäärä, arvosana)\n"
                + "VALUES ('999999995', '582103', '2014-06-1', 5);\n"
                + "INSERT INTO Kurssisuoritus (opiskelija, kurssi, päivämäärä, arvosana)\n"
                + "VALUES ('999999997', '581328', '2016-06-1', 4);\n"
                + "INSERT INTO Kurssisuoritus (opiskelija, kurssi, päivämäärä, arvosana)\n"
                + "VALUES ('999999994', '581328', '2010-06-1', 2);\n"
                + "INSERT INTO Kurssisuoritus (opiskelija, kurssi, päivämäärä, arvosana)\n"
                + "VALUES ('999999999', '581325', '2016-10-1', 4);\n";

        String tehtava = "\nCREATE TABLE Tehtävä\n"
                + "(tunnus INT GENERATED BY DEFAULT AS IDENTITY (START WITH 0), nimi VARCHAR, kuvaus VARCHAR,\n"
                + "PRIMARY KEY (tunnus));\n"
                + "INSERT INTO Tehtävä (nimi, kuvaus)\n"
                + "VALUES ('Hello world', 'Ohjelmointi esittäytyy');\n"
                + "INSERT INTO Tehtävä (nimi, kuvaus)\n"
                + "VALUES ('Game of life', 'Elämää');\n"
                + "INSERT INTO Tehtävä (nimi, kuvaus)\n"
                + "VALUES ('Sääasema', 'Anturit kuntoon');\n"
                + "INSERT INTO Tehtävä (nimi, kuvaus)\n"
                + "VALUES ('SELECT', 'Valitse kaikki');\n"
                + "INSERT INTO Tehtävä (nimi, kuvaus)\n"
                + "VALUES ('Eläintarha', 'Rajapintoja');\n";

        String kurssitehtava = "\nCREATE TABLE Kurssitehtävä (tunnus INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 0), tehtävä INTEGER, kurssi VARCHAR,\n"
                + "PRIMARY KEY (tunnus),\n"
                + "FOREIGN KEY (tehtävä) REFERENCES Tehtävä (tunnus),\n"
                + "FOREIGN KEY (kurssi) REFERENCES Kurssi (kurssitunnus));\n"
                + "INSERT INTO Kurssitehtävä (tehtävä, kurssi)\n"
                + "VALUES (3, '582103');\n"
                + "INSERT INTO Kurssitehtävä (tehtävä, kurssi)\n"
                + "VALUES (2, '581325');\n"
                + "INSERT INTO Kurssitehtävä (tehtävä, kurssi)\n"
                + "VALUES (4, '581325');\n"
                + "INSERT INTO Kurssitehtävä (tehtävä, kurssi)\n"
                + "VALUES (0, '581328');\n"
                + "INSERT INTO Kurssitehtävä (tehtävä, kurssi)\n"
                + "VALUES (1, '581328');\n"
                + "INSERT INTO Kurssitehtävä (tehtävä, kurssi)\n"
                + "VALUES (2, '581328');\n";

        createTable = new StringBuilder();
        createTable.append(opiskelija).append(kurssi).append(kurssisuoritus2).append(tehtava)
                .append(kurssitehtava);
        databaseService.createDatabase("Yliopisto2", createTable.toString());

        String tehtavasuoritus = "\nCREATE TABLE Tehtäväsuoritus (opiskelija VARCHAR, tehtävä INTEGER, aika Date,\n"
                + "FOREIGN KEY (opiskelija) REFERENCES Opiskelija(opiskelijanumero),\n"
                + "FOREIGN KEY (tehtävä) REFERENCES Kurssitehtävä(tunnus));"
                + "INSERT INTO Tehtäväsuoritus (opiskelija, tehtävä, aika)\n"
                + "VALUES (999999998, 3, '2009-09-05');\n"
                + "INSERT INTO Tehtäväsuoritus (opiskelija, tehtävä, aika)\n"
                + "VALUES (999999998, 4, '2015-09-17');\n"
                + "INSERT INTO Tehtäväsuoritus (opiskelija, tehtävä, aika)\n"
                + "VALUES (999999997, 0, '2015-09-01');\n"
                + "INSERT INTO Tehtäväsuoritus (opiskelija, tehtävä, aika)\n"
                + "VALUES (999999997, 1, '2016-02-05');\n";

        createTable.append(tehtavasuoritus);
        databaseService.createDatabase("Yliopisto3", createTable.toString());

        //Kolmas kategoria
        Category category2 = createCategory("Erilaiset yhteystyypit", "Käydään läpi tietokannan erilaiset yhteystyypit ja tutustutaan eri taulujen sisällön yhdistämiseen");

        createTask("Kahden taulun yhdistäminen - 1", "Tee kysely, joka yhdistää taulut Opiskelija ja Kurssisuoritus opiskelijanumeron perusteella.",
                databaseRepository.findByNameAndDeletedFalse("Yliopisto2").get(0),
                "SELECT * FROM Opiskelija o, Kurssisuoritus k\n"
                + "WHERE o.opiskelijanumero = k.opiskelija;",
                category2);
        createTask("Kahden taulun yhdistäminen - 2", "Tee kysely, joka tulostaa opiskelijan nimen, kurssisuorituksen päivämäärän ja arvosanan.",
                databaseRepository.findByNameAndDeletedFalse("Yliopisto2").get(0),
                "SELECT o.nimi, k.päivämäärä, k.arvosana FROM Opiskelija o, Kurssisuoritus k\n"
                + "WHERE o.opiskelijanumero = k.opiskelija;",
                category2);
        createTask("Useamman taulun yhdistäminen - 1", "Tulosta opiskelijoiden nimet ja heidän suorittamien kurssien nimet. Nimeä opiskelijan nimi-sarake 'opiskelija':ksi ja kurssin nimi 'kurssi':ksi.",
                databaseRepository.findByNameAndDeletedFalse("Yliopisto2").get(0),
                "SELECT DISTINCT o.nimi AS opiskelija, k.nimi AS kurssi FROM Opiskelija o, Kurssi k, Kurssisuoritus ks\n"
                + "WHERE o.opiskelijanumero = ks.opiskelija\n"
                + "AND ks.kurssi AND k.kurssitunnus;",
                category2);

        createTask("Useamman taulun yhdistäminen - 2", "Tulosta Arton MAHDOLLISESTI tehtyjen tehtävien nimet. Huomaa että opiskelijan varmasti tehtyjen selvittäminen ei tässä tietokantatoteutuksessa ole mahdollista. Pohdi miksi näin on.",
                databaseRepository.findByNameAndDeletedFalse("Yliopisto2").get(0),
                "SELECT t.nimi FROM Tehtävä t, Kurssitehtävä kt, Kurssi k, Kurssisuoritus ks, Opiskelija o \n"
                + "WHERE t.tunnus = kt.tehtävä \n"
                + "AND kt.kurssi = k.kurssitunnus \n"
                + "AND k.kurssitunnus = ks.kurssi \n"
                + "AND ks.opiskelija = o.opiskelijanumero \n"
                + "AND o.nimi = 'Arto';",
                category2);

        createTask("Tehdyt tehtävät", "Nyt tietokantaan on lisätty uusi taulu. Hae nyt opiskelijoiden tehdyt tehtävät siten että ensimmäinen sarake on opiskelijan nimi 'opiskelija' ja seuraava sarake on nimeltään 'tehtävä', jossa on tehtävän nimi",
                databaseRepository.findByNameAndDeletedFalse("Yliopisto3").get(0),
                "SELECT o.nimi AS opiskelija, t.nimi AS kurssi FROM Tehtäväsuoritus ts, Kurssitehtävä ks, Tehtävä t, Opiskelija o\n"
                + "WHERE ts.tehtävä = ks.tunnus\n"
                + "AND ks.tehtävä = t.tunnus\n"
                + "AND ts.opiskelija = o.opiskelijanumero;",
                category2);

        createTask("Iidan tehtävät", "Etsi Iidan tekemien tehtävien nimet. Jollet ole jo kokeillut, kokeile käyttää INNER JOIN-kyselyä.",
                databaseRepository.findByNameAndDeletedFalse("Yliopisto3").get(0),
                "SELECT t.nimi FROM Tehtävä t\n"
                + "INNER JOIN Kurssitehtävä kt ON t.tunnus = kt.tehtävä\n"
                + "INNER JOIN Tehtäväsuoritus ts ON kt.tunnus = ts.tehtävä\n"
                + "INNER JOIN Opiskelija o ON ts.opiskelija = o.opiskelijanumero AND o.nimi = 'Iida';",
                category2);

        createTask("Virheellinen opiskelija", "Tietokantaan on lisätty ylimääräinen opiskelija nimeltään Vampyyri. Poista merkintä tietokannasta.",
                databaseRepository.findByNameAndDeletedFalse("Yliopisto3").get(0),
                "DELETE FROM Opiskelija WHERE nimi = 'Vampyyri';",
                category2);

        createTask("Opiskelijan lisääminen", "Lisää itsesi tietokannan opiskelija-tauluun.",
                databaseRepository.findByNameAndDeletedFalse("Yliopisto3").get(0),
                "INSERT INTO Opiskelija (opiskelijanumero, nimi, syntymävuosi, pääaine) VALUES ('123456789', 'Matti', 1993, 'Tietojenkäsittelytiede');",
                category2);

        //Neljäs kategoria
        Category category3 = createCategory("Ali- ja yhteenvetokyselyt", "Useamman tietokantataulun yhdistelyn jälkeen on aika rajata hakutuloksia erilaisilla ehdoilla.");

        createTask("Laiskat opiskelijat", "Hae tietokannasta opiskelijoiden opiskelijanumero ja nimi, joilla ei ole yhtäkään kurssisuoritusta.",
                databaseRepository.findByNameAndDeletedFalse("Yliopisto3").get(0),
                "SELECT opiskelijanumero, nimi FROM Opiskelija o\n"
                + "LEFT JOIN Kurssisuoritus k\n"
                + "ON o.opiskelijanumero = k.opiskelija\n"
                + "WHERE k.kurssi IS null",
                category3);

        createTask("Kursseja", "Listaa kurssit, joilla ei ole yhtäkään tehtäviä",
                databaseRepository.findByNameAndDeletedFalse("Yliopisto3").get(0),
                "SELECT * FROM Kurssi k\n"
                + "WHERE k.kurssitunnus\n"
                + "NOT IN (SELECT kurssi FROM Kurssisuoritus);",
                category3);

        createTask("Tehdyt ja tekemättömät kurssit", "Kasper haluaa nähdä tekemiensä "
                + "kurssien arvosanan, ja kurssit joita hän ei ole vielä suorittanut. "
                + "Suorita kysely niin että ensimmäisessä sarakkeessa on kurssin tunnus, "
                + "ja toisessa kurssisuorituksen mahdollinen arvosana.",
                databaseRepository.findByNameAndDeletedFalse("Yliopisto3").get(0),
                "SELECT k.kurssitunnus, ks.arvosana FROM Kurssi k\n"
                + "LEFT JOIN Kurssisuoritus ks ON k.kurssitunnus = ks.kurssi \n"
                + "AND ks.opiskelija IN (SELECT o.opiskelijanumero \n"
                + "    FROM Opiskelija o \n"
                + "    WHERE o.nimi = 'Kasper');",
                category3);

        createTask("Yhteenvetokyselyt - 1", "Listaa opiskelijoiden pääaineet ja niitä opiskelevien lukumäärät.",
                databaseRepository.findByNameAndDeletedFalse("Yliopisto3").get(0),
                "SELECT pääaine, COUNT(*) AS lukumäärä\n"
                + "    FROM Opiskelija GROUP BY pääaine;",
                category3);

        createTask("Yhteenvetokyselyt - 2", "Laske kurssisuoritustaulussa olevat "
                + "kurssisuoritukset kurssitunnuksen perusteella. Nimeä sarakkeet "
                + "uudelleen nimillä \"kurssi\" ja \"suorituksia\".",
                databaseRepository.findByNameAndDeletedFalse("Yliopisto3").get(0),
                "SELECT k.nimi AS kurssi, COUNT(ks.kurssi) AS suorituksia FROM Kurssi k LEFT JOIN Kurssisuoritus ks\n"
                + "ON k.kurssitunnus = ks.kurssi GROUP BY k.nimi",
                category3);

        createTask("Opiskelijoiden keskiarvot", "Suorita kysely joka antaa vastaukseksi opiskelijan opiskelijanumeron ja hänen kurssisuoritusten keskiarvon. Nimeä sarakkeet uudelleen nimillä \"opiskelija\" ja \"keskiarvo\"",
                databaseRepository.findByNameAndDeletedFalse("Yliopisto3").get(0),
                "SELECT o.opiskelijanumero opiskelija, AVG(ks.arvosana) keskiarvo \n"
                + "FROM Opiskelija o\n"
                + "LEFT JOIN Kurssisuoritus ks ON ks.opiskelija = o.opiskelijanumero\n"
                + "GROUP BY o.opiskelijanumero;",
                category3);

        createTask("Kurssien keskiarvot", "Laske nyt kurssien keskiarvot. Anna sarakkeille nimet \"kurssi\" ja \"keskiarvo\" Järjestä vastaustaulu kurssin arvosanan mukaan laskevaan järjestykseen.",
                databaseRepository.findByNameAndDeletedFalse("Yliopisto3").get(0),
                "SELECT k.nimi kurssi, AVG(ks.arvosana) keskiarvo FROM Kurssi k\n"
                + "INNER JOIN Kurssisuoritus ks ON ks.kurssi = k.kurssitunnus\n"
                + "GROUP BY k.nimi\n"
                + "ORDER BY keskiarvo DESC;",
                category3);

        databaseService.createDatabase("Hiekkalaatikko", "");
        //Viides kategoria
        Category category4 = createCategory("Tietokannan luominen ja muokkaaminen", "Harjoitellaan tietokannan luomista ja muokkaamista. Huomaa että SQL-tasker palauttaa tietokantaa muokatessa kyseisen tietokannan sisällön.");

        createTask("Tietokantatauluun lisääminen", "Lisää itsesi opiskelijoiden listaan. Sen lisäksi merkitse itsellesi kurssisuoritus ja tehtäväsuoritus.",
                databaseRepository.findByNameAndDeletedFalse("Yliopisto3").get(0),
                "INSERT INTO Opiskelija (opiskelijanumero, nimi, syntymävuosi, pääaine) VALUES ('123456789', 'Matti', 2016, 'Käpistely');\n"
                + "INSERT INTO Kurssisuoritus (arvosana, opiskelija, kurssi) VALUES (5, '123456789', '581325');\n"
                + "INSERT INTO Tehtäväsuoritus (opiskelija, tehtävä) VALUES ('123456789', 2);",
                category4);

        createTask("Tietokantataulun luominen", "Luo uusi tietokantataulu Kirjahylly "
                + "sarakkeilla id, nimi, kirjailija, julkaisuvuosi ja sivumäärä. "
                + "Lisää kirjahyllyyn kaksi kirjaa.",
                databaseRepository.findByNameAndDeletedFalse("Hiekkalaatikko").get(0),
                "CREATE TABLE Kirjahylly(id INTEGER, nimi VARCHAR, kirjailija VARCHAR, julkaisuvuosi INTEGER(4), sivumäärä INTEGER);\n"
                + "INSERT INTO Kirjahylly (id, nimi, kirjailija, julkaisuvuosi, sivumäärä) VALUES (0, 'Machine learning', 'Peter Flach', 2015, 342);"
                + "INSERT INTO Kirjahylly (id, nimi, kirjailija, julkaisuvuosi, sivumäärä) VALUES (1, 'Readme', 'Matti Meikeläinen', 2016, 15);",
                category4);

        createTask("Tietokantataulun muokkaaminen", "Tietokannan opiskelija-taulun "
                + "nimi-sarakkeen lisäksi halutaan lisätä erikseen \"sukunimi\"-sarake. "
                + "Asian selkeyttämiseksi nimeä sarake \"nimi\" uudelleen nimellä \"etunimi\". "
                + "Suorita kyselyt.",
                databaseRepository.findByNameAndDeletedFalse("Yliopisto3").get(0),
                "ALTER TABLE Opiskelija ADD COLUMN sukunimi VARCHAR(255);\n"
                + "ALTER TABLE Opiskelija ALTER COLUMN nimi RENAME TO etunimi;",
                category4);

        course = new Course();
        course.setName("Tietokantojen perusteet - 0000");
        course.setDescription("Käyttövalmis esimerkkikurssi. SQL-Tasker käyttää SQL:n murretta hsql. Hsql:n syntaksiohjeet löytyvät osoitteesta: http://www.hsqldb.org/doc/guide/ch09.html");
        course.setCourseCategories(Arrays.asList(category1, category2, category3, category4));
        course = courseRepository.save(course);

        details = new CategoryDetail(course, category1, null, null);
        detailRepository.save(details);
        details = new CategoryDetail(course, category2, null, null);
        detailRepository.save(details);
        details = new CategoryDetail(course, category3, null, null);
        detailRepository.save(details);
        details = new CategoryDetail(course, category4, null, null);
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

package wepaht.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class Database extends AbstractPersistable<Long> {

    private String name;

    @Lob
    private String databaseSchema;

    /**
     *
     * @return get name of database.
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name set name of the database.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return get scheme of the database.
     */
    public String getDatabaseSchema() {
        return databaseSchema;
    }

    /**
     *
     * @param databaseSchema set scheme of the database.
     */
    public void setDatabaseSchema(String databaseSchema) {
        this.databaseSchema = databaseSchema;
    }
}

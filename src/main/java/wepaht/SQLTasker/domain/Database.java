package wepaht.SQLTasker.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class Database extends AbstractPersistable<Long> implements Owned {

    @NotEmpty
    private String name;

    @Lob
    private String databaseSchema;
    
    @ManyToOne
    private TmcAccount owner;
    
    private Boolean deleted;
    
    public Database() {
        this.deleted = false;
    }

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

    @Override
    public TmcAccount getOwner() {
        return owner;
    }

    @Override
    public void setOwner(TmcAccount owner) {
        this.owner = owner;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}

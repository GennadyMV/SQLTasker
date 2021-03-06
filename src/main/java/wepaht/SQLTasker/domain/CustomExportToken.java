package wepaht.SQLTasker.domain;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class CustomExportToken extends AbstractPersistable<Long>{

    @Column(unique = true)
    private String token;

    @OneToOne
    private TmcAccount user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = RandomStringUtils.randomAlphabetic(30);
    }

    public TmcAccount getUser() {
        return user;
    }

    public void setUser(TmcAccount user) {
        this.user = user;
    }
}

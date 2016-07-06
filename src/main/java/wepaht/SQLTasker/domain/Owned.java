package wepaht.SQLTasker.domain;

public interface Owned {
    
    void setOwner(TmcAccount Owner);
    Account getOwner();
}

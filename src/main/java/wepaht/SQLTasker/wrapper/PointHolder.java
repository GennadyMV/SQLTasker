package wepaht.SQLTasker.wrapper;

/**
 * Simple class for rest-controller to hold user and points
 */
public class PointHolder {

    private String username;
    private Long points;

    public PointHolder(String username, Long points) {
        this.username = username;
        this.points = points;
    }
    
    public String getUsername() {
        return username;
    }

    public Long getPoints() {
        return points;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPoints(Long points) {
        this.points = points;
    }
}

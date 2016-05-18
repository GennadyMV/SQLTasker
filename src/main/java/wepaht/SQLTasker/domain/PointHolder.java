package wepaht.SQLTasker.domain;

/**
 * Simple class for rest-controller to hold user and points
 */
public class PointHolder {

    private String username;
    private Integer points;

    public String getUsername() {
        return username;
    }

    public Integer getPoints() {
        return points;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}

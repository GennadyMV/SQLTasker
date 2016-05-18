package wepaht.SQLTasker.domain;


import java.util.ArrayList;
import java.util.List;

/**
 * Helper-object to hold for asked table's columns and rows. Intended to ease usage with views.
 */
public class Table {

    private String name;
    private List<String> columns;
    private List<List<String>> rows;

    public Table(String name) {
        this.name = name.toUpperCase();
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }
}

package Classroom;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AssignmentData {
    /** This class is an entity class that stores and gets an assignment's id, the class the assignment belongs to, and
     * the name of the assignment for the purposes of populating the table in StudentMarks.
     **/
    private final StringProperty ID;
    private final StringProperty name;
    private final StringProperty classes;

    public String getID() {
        return ID.get();
    }

    public void setID(String ID) {
        this.ID.set(ID);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getClasses() {
        return classes.get();
    }

    public void setClasses(String classes) {
        this.classes.set(classes);
    }

    public AssignmentData(String id, String name, String classes) {
        this.ID = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.classes = new SimpleStringProperty(classes);
    }
}

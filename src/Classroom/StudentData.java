package Classroom;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StudentData {
    /** This class is an entity class that stores and gets a student's ID, the student's name, and the classrooms
     * the student is in for the purposes of populating the table in the students tab in the classroom page.
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

    public StudentData(String id, String name, String classes) {
        this.ID = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.classes = new SimpleStringProperty(classes);
    }
}

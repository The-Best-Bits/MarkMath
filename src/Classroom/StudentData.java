package Classroom;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StudentData {
    private final StringProperty ID;
    private final StringProperty name;
    private final StringProperty classes;

    public String getID() {
        return ID.get();
    }

    public StringProperty IDProperty() {
        return ID;
    }

    public void setID(String ID) {
        this.ID.set(ID);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getClasses() {
        return classes.get();
    }

    public StringProperty classesProperty() {
        return classes;
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

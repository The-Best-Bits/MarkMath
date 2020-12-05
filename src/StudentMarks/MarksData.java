package StudentMarks;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MarksData {
    private final StringProperty assignmentID;
    private final StringProperty assignmentName;
    private final StringProperty grade;

    public String getAssignmentID() {
        return assignmentID.get();
    }

    public StringProperty assignmentIDProperty() {
        return assignmentID;
    }

    public void setAssignmentID(String assignmentID) {
        this.assignmentID.set(assignmentID);
    }

    public String getAssignmentName() {
        return assignmentName.get();
    }

    public StringProperty assignmentNameProperty() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName.set(assignmentName);
    }

    public String getGrade() {
        return grade.get();
    }

    public StringProperty gradeProperty() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade.set(grade);
    }

    public MarksData(String id, String name, String classes) {
        this.assignmentID = new SimpleStringProperty(id);
        this.assignmentName = new SimpleStringProperty(name);
        this.grade = new SimpleStringProperty(classes);
    }
}

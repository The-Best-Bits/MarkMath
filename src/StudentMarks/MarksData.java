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

    public String getAssignmentName() {
        return assignmentName.get();
    }


    public void setAssignmentName(String assignmentName) {
        this.assignmentName.set(assignmentName);
    }

    public String getGrade() {
        return grade.get();
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

package Assignments;

import javafx.beans.property.*;
import javafx.beans.value.ObservableMapValue;
import javafx.collections.ObservableMap;

public class GradeData {
    private final StringProperty ID;
    private final StringProperty name;
    private final FloatProperty grade;
//    private final MapProperty<String, Float> breakdown;

    public GradeData(String student_id, String student_name, float grade, ObservableMap<String, Float> breakdown){
        this.ID = new SimpleStringProperty(student_id);
        this.name = new SimpleStringProperty(student_name);
        this.grade = new SimpleFloatProperty(grade);
//        this.breakdown = new SimpleMapProperty<>(breakdown);

    }

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

    public float getGrade() {
        return grade.get();
    }

    public FloatProperty gradeProperty() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade.set(grade);
    }

//    public ObservableMapValue<String, Float> getBreakdown() {
//        return (ObservableMapValue<String, Float>) breakdown.get();
//    }

//    public MapProperty<String, Float> breakdownProperty() {
//        return breakdown;
//    }

//    public void setBreakdown(ObservableMap<String, Float> breakdown) {
//        this.breakdown.set(breakdown);
//    }
}

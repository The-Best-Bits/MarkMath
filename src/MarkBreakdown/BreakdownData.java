package MarkBreakdown;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BreakdownData {
    private final StringProperty possibleMark;
    private final StringProperty mark;
    private final StringProperty questionNumber;

    public String getPossibleMark() {
        return possibleMark.get();
    }

    public StringProperty possibleMarkProperty() {
        return possibleMark;
    }

    public void setPossibleMark(String possibleMark) {
        this.possibleMark.set(possibleMark);
    }

    public String getQuestionNumber() {
        return questionNumber.get();
    }

    public StringProperty questionNumberProperty() {
        return questionNumber;
    }

    public void setQuestionNumber(String questionNumber) {
        this.questionNumber.set(questionNumber);
    }

    public String getMark() {
        return mark.get();
    }

    public StringProperty markProperty() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark.set(mark);
    }

    public BreakdownData(String questionNumber, String mark, String possibleMark) {
        this.questionNumber = new SimpleStringProperty(questionNumber);
        this.mark = new SimpleStringProperty(mark);
        this.possibleMark = new SimpleStringProperty(possibleMark);
    }
}
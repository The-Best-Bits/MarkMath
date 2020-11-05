package markmath.usecases;

import markmath.entities.*;

import java.util.ArrayList;

public class StudentAssignmentManager {
    /**
     *  Manages a single student assignment by creating the copy, adding questions,
     *  marking them, and putting them in the correcr bundle.
     *
     *  Attributes:
     *  carbonCopy: the copy of a StudentAssignment
     *  studentID: the id of the student owner
     *  studentName: the name of the student owner
     */

    private StudentAssignment carbonCopy;
    private int studentID;
    private String studentName;

    public StudentAssignmentManager(int studentID, String name){
        this.studentID = studentID;
        studentName = name;
        carbonCopy = new StudentAssignment(studentID, name);
    }

    public void setCopy(String assignmentName, AssignmentOutline outline){
        carbonCopy.setAssignmentName(assignmentName);
        carbonCopy.setOutline(outline);
    }

    public void addQuestion(int No, int errors){
        ArrayList<Question> ques = carbonCopy.getQuestions();
        for(Question q: ques){
            if(No == q.getQuestionNumber()){
                carbonCopy.modifyQuestionErrors(No, errors);
                return;
            }
        }
        Question newQues = new Question(No, errors);
        carbonCopy.addQuestion(newQues);
    }

    public void markAllQuestions(){
        ArrayList<Question> ques = carbonCopy.getQuestions();
        for(Question q: ques){
            MarkingCalculator calculator = new MarkingCalculator(q);
            carbonCopy.setFinalMarkSingleQuestion(
                    q.getQuestionNumber(), calculator.getMark());
        }
    }

    public void matchBundle(Classroom room){
        room.getAssignmentBundle(
                carbonCopy.getAssignmentName()).addStudentAssignment(carbonCopy);
    }

    public StudentAssignment getCopy(){
        return carbonCopy;
    }
}

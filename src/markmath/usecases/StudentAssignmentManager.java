package markmath.usecases;

import markmath.entities.*;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentAssignmentManager {
    /**
     *  Manages a single student assignment by creating the copy, adding questions,
     *  marking them, and putting them in the correct bundle.
     *
     *  Attributes:
     *  carbonCopy: the copy of a StudentAssignment
     *  studentID: the id of the student owner
     *  studentName: the name of the student owner
     */

    private StudentAssignment carbonCopy;
    private int studentID;
    private String studentName;


    public StudentAssignmentManager(int studentID, String studentName, String assignmentName, String assignmentType,
                                    HashMap<String, Integer> finalParsedData, AssignmentOutline outline){
        this.studentID = studentID;
        this.studentName = studentName;
        carbonCopy = new StudentAssignment(studentID, studentName, assignmentType, assignmentName);
        carbonCopy.setOutline(outline);
        this.addQuestions(finalParsedData);
    }

//    /**
//    Sets the assignment outline for the StudentAssignment this StudentAssignmentManager manages
//     */
//    //changed
//    //we have the assignmentType of this document so why don't we just set the assignment outline ourself? No need for
//    //this method. But how do we get the assignment outline corresponding?
//    public void setCopy(AssignmentOutline outline){
//        //removed line
//        carbonCopy.setOutline(outline);
//    }

    /**
    Uses the parsed data to add all of the Questions to the StudentAssignment this StudentAssignmentManager manages
    Pre condition: The outline for this carbon copy must be set before this method is called
     */
    private void addQuestions(HashMap<String, Integer> finalParsedData){
        HashMap<String, Float> questionToMarks = carbonCopy.getOutline().getQuestionToMarks();
        int numOfQuestions = questionToMarks.size();
        int i =1;
        while (i<= numOfQuestions){
            if (finalParsedData.containsKey("question" + i)){
                Question tempQ = new Question(i, finalParsedData.get("question" + i));
                carbonCopy.addQuestion(tempQ);
            }
            else
            {
                Question tempQ = new Question(i, 0);
                carbonCopy.addQuestion(tempQ);
            }
            i+=1;
        }
    }

//    //previous implementation
//    public void addQuestion(int No, int errors){
//        ArrayList<Question> ques = carbonCopy.getQuestions();
//        for(Question q: ques){
//            if(No == q.getQuestionNumber()){
//                carbonCopy.modifyQuestionErrors(No, errors);
//                return;
//            }
//        }
//        Question newQues = new Question(No, errors);
//        carbonCopy.addQuestion(newQues);
//    }

    /**
     * Marks all of the Questions in the StudentAssignment this StudentAssignmentManager manages, and sets the final
     * mark for this StudentAssignment
     */
    public void markAllQuestions(){
        ArrayList<Question> ques = carbonCopy.getQuestions();
        HashMap<String, Float> outline = carbonCopy.getOutline().getQuestionToMarks();
        for(Question q: ques){
            MarkingCalculator calculator = new MarkingCalculator(q);
            carbonCopy.setFinalMarkSingleQuestion(
                    q.getQuestionNumber(), calculator.getMark(outline.get("question" + q.getQuestionNumber())));
        }
        carbonCopy.setFinalMark();
    }

    /**
    Adds the StudentAssignment this StudentAssignmentManager manages to the corresponding AssignmentBundle
     in the given classroom
     */
    public void matchBundle(Classroom room){
        room.getAssignmentBundle(
                carbonCopy.getAssignmentType()).addStudentAssignment(carbonCopy);
    }

    /**
     * Returns the StudentAssignment of this StudentAssignmentManager
     * @return carbonCopy
     */
    public StudentAssignment getCarbonCopy(){
        return carbonCopy;
    }
}

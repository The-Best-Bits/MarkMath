package markmath.usecases;

import entities.*;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentAssignmentManager {
    /**
     *  Following Clean Architecture this is a use case class that manages a single student assignment by creating
     *  the student assignment, adding questions to this student assignment, and marking the questions
     *
     *  Attributes:
     *  carbonCopy: the copy of a StudentAssignment
     *  studentID: the id of the student owner
     *  studentName: the name of the student owner
     */

    private StudentAssignment carbonCopy;
    private String studentID;
    private String studentName;


    public StudentAssignmentManager(String studentID, String studentName, String assignmentName, String assignmentType,
                                    HashMap<String, Integer> finalParsedData, AssignmentOutline outline){
        this.studentID = studentID;
        this.studentName = studentName;
        carbonCopy = new StudentAssignment(studentID, studentName, assignmentType, assignmentName);
        carbonCopy.setOutline(outline);
        this.addQuestions(finalParsedData);
    }

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
     * Returns the StudentAssignment of this StudentAssignmentManager
     * @return carbonCopy
     */
    public StudentAssignment getCarbonCopy(){
        return carbonCopy;
    }
}

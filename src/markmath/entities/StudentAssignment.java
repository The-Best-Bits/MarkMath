package markmath.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class StudentAssignment {
    /**
     * Following Clean Architecture, this is an entity class that represents a students completed assignment
     * studentName: name of this student owner
     * studentID: ID of this student owner
     * assignmentType: name of the AssignmentBundle this student assignment it belongs to
     * assignmentName: name of this assignment, concatenated by studentID and assignmentType
     * questions: the list of all questions contained
     * outline: the mapping of question to full mark
     * finalMarkBreakdown: the mapping of question to final mark
     * fullMark: the total full mark
     * finalMark: the total final mark
     */

    private String studentName;
    private String assignmentType;
    private String assignmentName;
    private String studentID;
    private ArrayList<Question> questions = new ArrayList<>();
    private AssignmentOutline outline;
    private LinkedHashMap<String, Float> finalMarkBreakdown;
    private float fullMark = 0;
    private float finalMark = 0;

    /**
     * Create a StudentAssignment with given student ID and name */
    public StudentAssignment(String studentID, String studentName, String assignmentType, String assignmentName){
        this.studentID = studentID;
        this.studentName = studentName;
        this.assignmentType = assignmentType;
        this.assignmentName = assignmentName;

    }

    /**
     * Another way to create StudentAssignment with student ID, name, and grade data from database */
    public StudentAssignment(String studentID, String studentName, float total, LinkedHashMap<
            String, Float> map1){
        this.studentID = studentID;
        this.studentName = studentName;
        this.finalMark = total;
        this.finalMarkBreakdown = map1;
    }


    public AssignmentOutline getOutline() {return outline;}

    public LinkedHashMap<String, Float> getFinalMarkBreakdown(){return finalMarkBreakdown;}

    public float getFinalMark(){return finalMark;}

    public float getFullMark(){return fullMark;}

    public String getStudentID(){return studentID;}

    public String getAssignmentName(){return this.assignmentName;}

    public String getAssignmentType(){return this.assignmentType;}

    public ArrayList<Question> getQuestions(){return questions;}

    public String getStudentName(){return this.studentName;}

    /**
     * Get the string of the mapping of question to final mark */
    public String getBreakdownString(){
        return finalMarkBreakdown.keySet().stream().map(
                key -> key + ": " + finalMarkBreakdown.get(key)).collect(
                Collectors.joining(", "));
    }

    /**
     * Get the Question object with given question ID
     * @param questionNum the number of the Question
     * @return the Question in this StudentAssignment with the given question number
     */
    public Question getQuestion(int questionNum){
        for (int i =0; i < questions.size(); i++){
            if (questions.get(i).getQuestionNumber() == questionNum){
                return questions.get(i);
            }
        }
        return null;
    }

    public void setAssignmentName(String assignmentName){this.assignmentName = assignmentName;}

    public void addQuestion(Question ques){questions.add(ques);}

    /**
     * Set the outline and full mark calculated from the outline */
    public void setOutline(AssignmentOutline outline) {
        this.outline = outline;
        this.fullMark = outline.returnFullMark();
    }

    /**
     * Modifies the specified question (by question number) to have a different amount of errors
     * Pre condition: there must be a question in this student assignment with the given number
     * @param No question number
     * @param error new number of errors for the question
     */
    public void modifyQuestionErrors(int No, int error){
        for(Question q: questions){
            if(q.getQuestionNumber() == No){
                q.setErrors(error);
            }
        }
    }

    /**
     * Set the final mark for a single question in this assignment
     * @param No question ID
     * @param mark the final mark for this question
     */
    public void setFinalMarkSingleQuestion(int No, float mark){
        for(Question q: questions) {
            if (q.getQuestionNumber() == No) {
                q.setFinalMark(mark);
            }
        }
    }

    /**
     * Sets the final mark of this StudentAssignment
     * Note: Should only be called once every Question in questions has been marked. If this is not the
     * case, then the final mark will be lower than expected, as the default mark for each question is 0
     */
    public void setFinalMark(){
        float temp = 0;
        for(Question q: questions){
            System.out.println(q.getQuestionNumber());
            System.out.println(q.getNumberOfErrors());
            System.out.println(q.getFinalMark());
            temp += q.getFinalMark();
        }
        this.finalMark = temp;
        System.out.println(this.finalMark);
    }



}

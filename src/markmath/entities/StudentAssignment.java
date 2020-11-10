package markmath.entities;

import java.util.ArrayList;

public class StudentAssignment {
    /* It represents a single student assignment;
     * it stores its owner's name, all the questions contained in it,
     * the mapping of each question to corresponding marks (AssignmentOutline),
     * and the total full mark of this assignment.
     */

    /* Attributes
     * studentName: name of this student owner
     * AssignmentName: name of the assignment name, consistent with the bundle it belongs to
     * studentID: ID of this student owner
     * questions: the list of all questions contained
     * outline: the mapping of question to mark
     * fullMark: the total full mark
     * finalMark: the total final mark
     */

    private String studentName;
    //added assignmentName, changed AssignmentName to assignmentType
    private String assignmentType;
    private String assignmentName;
    private int studentID;
    private ArrayList<Question> questions = new ArrayList<>();
    //do we need to store the assignment outline in a student assignment?
    private AssignmentOutline outline;
    private float fullMark = 0;
    private float finalMark = 0;

    /* create a StudentAssignment with given student ID and name */
    public StudentAssignment(int studentID, String studentName, String assignmentType, String assignmentName){
        this.studentID = studentID;
        this.studentName = studentName;
        this.assignmentType = assignmentType;
        this.assignmentName = assignmentName;

    }


    public AssignmentOutline getOutline() {return outline;}

    public void setOutline(AssignmentOutline outline) {
        this.outline = outline;
        this.fullMark = outline.returnFullMark();
    }


    public float getFinalMark(){return finalMark;}

    public float getFullMark(){return fullMark;}

    public int getStudentID(){return studentID;}

    //changed
    public String getAssignmentName(){return this.assignmentName;}

    //added
    public String getAssignmentType(){return this.assignmentType;}

    public ArrayList<Question> getQuestions(){return questions;}

    //removed setAssignmentType method
    public void setAssignmentName(String assignmentName){this.assignmentName = assignmentName;}

    public void addQuestion(Question ques){questions.add(ques);}

    public void modifyQuestionErrors(int No, int error){
        for(Question q: questions){
            if(q.getQuestionNumber() == No){
                q.setErrors(error);
            }
        }
    }

    public void setFinalMarkSingleQuestion(int No, int mark){
        for(Question q: questions) {
            if (q.getQuestionNumber() == No) {
                q.setFinalMark(mark);
            }
        }
    }



}

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
    private String studentID;
    private ArrayList<Question> questions = new ArrayList<>();
    //do we need to store the assignment outline in a student assignment?
    private AssignmentOutline outline;
    private float fullMark = 0;
    private float finalMark = 0;

    /* create a StudentAssignment with given student ID and name */
    public StudentAssignment(String studentID, String studentName, String assignmentType, String assignmentName){
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

    public String getStudentID(){return studentID;}

    //changed
    public String getAssignmentName(){return this.assignmentName;}

    //added
    public String getAssignmentType(){return this.assignmentType;}

    public ArrayList<Question> getQuestions(){return questions;}

    public String getStudentName(){return this.studentName;}

    /**
     *
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

    //removed setAssignmentType method
    public void setAssignmentName(String assignmentName){this.assignmentName = assignmentName;}

    public void addQuestion(Question ques){questions.add(ques);}

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
    //added November 10
    public void setFinalMark(){
        int temp = 0;
        for(Question q: questions){
            temp += q.getFinalMark();
        }
        this.finalMark = temp;
    }



}

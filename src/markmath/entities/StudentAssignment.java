package markmath.entities;

import java.util.ArrayList;

public class StudentAssignment {
    /* It represents a single student assignment;
     * it stores its owner's name, all the questions contained in it,
     * the mapping of each question to corresponding marks (AssignmentOutline),
     * and the total full mark of this assignment.
     */

    /* Attributes
        studentName: name of this student owner
        AssignmentName: name of the assignment name, consistent with the bundle it belongs to
        questions: the list of all questions contained
        outline: the mapping of question to mark
        fullMark: the total full mark
        finalMark: the total final mark
     */

    private String studentName;
    private String AssignmentName;
    private ArrayList<Question> questions = new ArrayList<>();
    private AssignmentOutline outline;
    private int fullMark = 0;
    private int finalMark = 0;


    /* create a StudentAssignment with given student's and assignment's name */
    public StudentAssignment(String name1, String name2, AssignmentOutline outline, ArrayList<Question> list){
        this.studentName = name1;
        this.AssignmentName = name2;
        questions.addAll(list);
        this.outline = outline;
        for (Question x: list){
            fullMark += x.getFullMark();
            finalMark += x.getFinalMark();
        }
    }

    public AssignmentOutline getOutline() {return outline;}

    public void setOutline(AssignmentOutline outline) {this.outline = outline;}

    public int getFinalMark(){return finalMark;}

    public int getFullMark(){return fullMark;}

    public String getStudentName(){return studentName;}

    public String getAssignmentName(){return AssignmentName+ "_" +studentName;}

    public void setAssignmentName(String name){AssignmentName = name;}



}

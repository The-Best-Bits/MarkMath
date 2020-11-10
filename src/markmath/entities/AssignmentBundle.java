package markmath.entities;

import java.util.ArrayList;
import java.util.Random;



public class AssignmentBundle {
    /** The AssignmentBundle class.
     * It represents one type of Assignment created by the teacher; it
     * stores its AssignmentOutline that describes questions and marks for all individual
     * assignment stored in the Bundle.
     * It stores all the assignments made by the students corresponding to this AssignmentBundle assignment.
     */
    /* Attributes
     * name: name of the assignment
     * outline: map of Questions to total marks ( Question1 : 6, ...)
     * assignments: list of assignments contained
     * */
    private String name; /** Represents the name of the AssignmentBundle. Must be shared by all stores assignments. */
    private AssignmentOutline outline;/** the map of questions to total marks, described by an assignmentOutline. */
    private ArrayList<StudentAssignment> assignments;/** list of contained assignments in the Bundle. */

    /** Creates a default AssignmentBundle, with no given outline and random name. */
    public AssignmentBundle(){
        Random random_name = new Random();
        this.name = "New Assignment" + String.valueOf(random_name.nextInt(200));
        assignments = new ArrayList<StudentAssignment>();
        this.outline = null;
    }

    /** Creates an AssignmentBundle with specified name and outline. Starts with an empty collection of assignments.
     * @param receivedOutline The outline of this AssignmentBundle
     * */
    public AssignmentBundle(AssignmentOutline receivedOutline){

        this.name = receivedOutline.getName();
        this.outline = receivedOutline;
        assignments = new ArrayList<StudentAssignment>();
    }

    /** Returns the assignmentOutline of this bundle, that describes the total marks to each question.
     * @return The current assignmentOutline of the bundle.*/
    public AssignmentOutline getOutline() {
        return this.outline;
    }

    /** Returns the assignmentOutline of this bundle, that describes the total marks to each question.
     * @param newOutline The new outline of Marks for the AssignmentBundle. */
    public void setOutline(AssignmentOutline newOutline){
        this.outline = newOutline;
    }

    /**Changes the name of the assignmentBundle.
     NOTE: CHANGES THE NAMES OF ALL ASSIGNMENTS CONTAINED IN ASSIGNMENTS.
     WARNING: Hypatia Files with different name from the Bundle will not be interpreted as part of the Bundle anymore,
     and MarkMath will not be able to mark them. Avoid changing the name of the assignment if possible.
     * @param newName  The new name assigned to the AssignmentBundle.*/
    //Changes the name of the assignmentBundle.
    //NOTE: CHANGES THE NAMES OF ALL ASSIGNMENTS CONTAINED IN ASSIGNMENTS
    public void changeName(String newName){
        this.name = newName;
        if (assignments.size() > 0) {
            for (StudentAssignment assignment: assignments){
                assignment.setAssignmentName(newName);
            }
        }
    }

    /** Returns the name of the assignment of this bundle
     * @return The current name of the bundle.*/
    public String getName() {
        return this.name;
    }

    /** Returns the studentAssignment that belongs to the student specified by studentID. Throws an exception if the
     * assignments list is empty, or it does not contain the assignment of the specified student.
     * @param studentID The ID of the student used to look for their assignment.
     * @return The assignment in this bundle that belongs to the specified student.
     * @throws NoAssignmentFoundException No assignment was found for the specified student.*/
    public StudentAssignment getStudentAssignment(int studentID) throws NoAssignmentFoundException{

       if (assignments.size() != 0) {
           for (StudentAssignment assignment: assignments){
               if (assignment.getStudentID() == studentID) {
                   return assignment;
               }
           }
       }
       throw new NoAssignmentFoundException();
    }

    /**
     * Adds to the assignments of the AssignmentBundle a student assignment that shares the AssignmentBundle name.
     * @param studentAssignment The student assignment that is going to be added to the bundle.
     */
    public void addStudentAssignment(StudentAssignment studentAssignment){
        if (studentAssignment.getAssignmentType().equals(this.name)){
            this.assignments.add(studentAssignment);
        }
    }
}

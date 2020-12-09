package entities;

public class AssignmentBundle {
    /** Following Clean Architecture this is an entity class that represents an AssignmentBundle. This class
     * does not store all of the student assignments in this assignment bundle, rather it represents the "shell"
     * of an assignment bundle and stores its name and outline
     * Attributes:
     * name: name of the assignment
     * outline: map of Questions to total marks ( Question1 : 6, ...)
     */

    private String name;
    private AssignmentOutline outline;

    /** Creates an AssignmentBundle with specified name and outline. Starts with an empty collection of assignments.
     * @param receivedOutline The outline of this AssignmentBundle
     * */
    public AssignmentBundle(AssignmentOutline receivedOutline){

        this.name = receivedOutline.getName();
        this.outline = receivedOutline;
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

    /** Returns the name of the assignment of this bundle
     * @return The current name of the bundle.*/
    public String getName() {
        return this.name;
    }

}

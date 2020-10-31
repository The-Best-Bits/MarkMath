package markmath.entities;

import java.util.ArrayList;

public class Student {
    /**The Student class.
     * A student has a unique student id and a name.
     * A student also stores this student's assignments in an ArrayList<StudentAssignment>
     *Attributes
     * id: id of this Student. Each student has a unique id.
     * name: name of this Student
     * assignments: list of assignments of this Student
     */
    private int id;
    private String name;
    private ArrayList<StudentAssignment> assignments = new ArrayList<>();

    public Student(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<StudentAssignment> getAssignments() {
        return this.assignments;
    }

    /**Add <assignment> into this student's assignments iff this student's assignment does not contain <assignment>.
     *Return true iff assignment is added successfully.
     * @param assignment the StudentAssignment to be added.
     * @return boolean if assignment is successfully added.
     */
    public Boolean addAssignments(StudentAssignment assignment) {
        if (!this.assignments.contains(assignment)) {
            this.assignments.add(assignment);
            return true;
        } else return false;
    }

    /**return the StudentAssignment of the given assignmentName.
     * @param assignmentName the name of the assignment wanted.
     * @return StudentAssignment the assignment by the given name. Return null if not found.
     */

    public StudentAssignment getAssignment(String assignmentName){
        for (StudentAssignment a: this.assignments){
            if (a.getAssignmentName().equals(assignmentName)){
                return a;
            }
        }
        return null;
    }
}

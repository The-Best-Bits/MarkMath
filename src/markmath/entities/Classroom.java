package markmath.entities;
import java.util.ArrayList;

public class Classroom {
    /**The Classroom class.
     *Classroom has a name. It stores the list of students in the classroom.
     *It also stores the list of AssignmentBundle of this classroom.
     *Attributes:
     *name: name of this classroom
     *students: list of students in the class
     *assignmentBundles: list of assignment bundles in the class
     */
    private String name;
    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<AssignmentBundle> assignmentBundles = new ArrayList<>();

    public Classroom(String name){
        this.name = name;
    }

    public ArrayList<Student> getStudents(){
        return this.students;
    }

    public ArrayList<AssignmentBundle> getAssignmentBundles(){
        return this.assignmentBundles;
    }

    /**Return the AssignmentBundle with the given assignment name.
     *Return null if the assignment is not found
     * @param name The name of the assignment to be found
     * @return AssignmentBundle The AssignmentBundle of the given name.
     */
    public AssignmentBundle getAssignment(String name){
        for (AssignmentBundle a: this.assignmentBundles){
            if (a.getName().equals(name)){
                return a;
            }
        }
        return null;
    }

    /**Enrolls the student iff this student is not already in the class.
     *Return true iff the student is successfully enrolled.
     * @param student The student to be enrolled
     * @return boolean If the student is successfully enrolled
     */
    public boolean enroll(Student student){
        if (!this.students.contains(student)){
            this.students.add(student);
        }
        return false;
    }

    /**Add the given assignmentBundle to the classroom iff the assignmentBundle is not already in the classroom.
     *return true iff the given assignment is successfully added.
     * @param assignment The AssignmentBundle to be added
     * @return If the AssignmentBundle is successfully added
     */
    public boolean addAssignmentBundles(AssignmentBundle assignment){
        if (!this.assignmentBundles.contains(assignment)){
            this.assignmentBundles.add(assignment);
            return true;
        }
        return false;
    }
}

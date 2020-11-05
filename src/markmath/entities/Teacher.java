package markmath.entities;

import java.util.ArrayList;

public class Teacher {
    /**The Teacher class.
     * A Teacher has a username and a password.
     * A Teacher also stores the list of classrooms this teacher is in with ArrayList<Classroom>
     Attributes:
     * username: Username of this Teacher
     * password: Password of this account
     * classrooms: List of classrooms this teacher is in
     */
    private String username;
    private String password;
    private ArrayList<Classroom> classrooms = new ArrayList<>();

    public Teacher(String username, String password){
        this.username = username;
        this.password = password;
    }

    public boolean checkUsername(String inputUsername){
        return this.username.equals(inputUsername);
    }

    public boolean checkPassword(String inputPassword){
        return this.password.equals(inputPassword);
    }

    /**Add the given classroom to the teacher's classroom
     * Return true iff the classroom is successfully added.
     * Attributes
     * @param classroom The classroom to be added.
     * @return boolean If the classroom is successfully added.
     */
    public boolean addClassroom(Classroom classroom){
        if (! this.classrooms.contains(classroom)){
            this.classrooms.add(classroom);
            return true;
        }
        return false;

    }
}

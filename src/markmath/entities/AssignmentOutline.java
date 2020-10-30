package markmath.entities;

import java.util.HashMap;

/* The AssignmentOutline class.
* It represents the name of the AssignmentBundle with which it is associated,
* and the total marks assigned from the teacher to each question of the
* assignment.
*/
public class AssignmentOutline {
    private String name;
    private HashMap<String, Integer> questionToMarks;

    public AssignmentOutline(String name, HashMap<String, Integer> receivedMarks) {
        this.name = name;
        this.questionToMarks = receivedMarks;
    }


    // Method that changes the mark assignmed to a question to a new mark.
    public void changeQuestionMark(String question, int newMark) {
        this.questionToMarks.replace(question, newMark);
    }
}

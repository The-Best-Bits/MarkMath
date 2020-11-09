package markmath.entities;

import java.util.HashMap;

/** The AssignmentOutline class.
* It represents the name of the AssignmentBundle with which it is associated,
* and the total marks assigned from the teacher to each question of the
* assignment.
*/
public class AssignmentOutline {
    private String name;
    private HashMap<String, Float> questionToMarks;

    /**
     * Creates a new AssignmentOutline, with given name and total marks assigned to each question.
     * @param name The name of the assignmentOutline. Must be shared with the name of the AssignmentBundle.
     * @param receivedMarks The marks assigned to each question; has the form {question1: x, question2: y... }.
     *                      The strings of the map should be always of the form questionN, with N an integer
     *                      representing the question number.
     */
    public AssignmentOutline(String name, HashMap<String, Float> receivedMarks) {
        this.name = name;
        this.questionToMarks = receivedMarks;
    }


    /**
     * Changes a mark assigned to a question in the hashMap questionToMarks. If the question is not in the outline,
     * returns false. If the change is successful, return true.
     * @param question The question you want to change the mark of.
     * @param newMark The new mark that will be assigned to question.
     * @return boolean: True if the question is in the outline, and the change is succesful.
     */
    public boolean changeQuestionMark(String question, float newMark) {
        if (questionToMarks.containsKey(question)){
            this.questionToMarks.replace(question, newMark);
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Adds a question with mark "mark" to the AssignmentOutline. If the question is not in the outline,
     * returns false. If the change is successful, return true.
     * @param question The question you want to add to the outline.
     * @param mark The new mark that will be assigned to question.
     * @return boolean: True if the question is in the outline, and the change is succesful.
     */
    public boolean addQuestionMark(String question, float mark) {
        if (questionToMarks.containsKey(question)){
            return false;
        }
        else{
            questionToMarks.put(question, mark);
            return true;
        }
    }

    /**
     *
     * @return HashMap of questions to assigned weighting of the question
     */
    public HashMap<String, Float> getQuestionToMarks(){return this.questionToMarks;}

    /**
     * Returns the name of the AssignmentOutline.
     * @return The name of the AssignmentOutline.
     */
    public String getName(){return name;}

    /**
     * Changes the name attribute of the AssignmentOutline.
     * @param name The name we want to give to the assignmentOutline.
     */
    public void setName(String name){this.name = name;}

    /**
     * Returns the total mark of the assignment with this AssignmentOutline, equal to the sum of all the total marks
     * assigned to each question of AssignmentOutline.
     * @return The total mark of the assignment with this AssignmentOutline.
     */
    public float returnFullMark(){
    float total = 0;
        for (float value : questionToMarks.values()) {
            total = total + value;
        }
    return total;

    }
}
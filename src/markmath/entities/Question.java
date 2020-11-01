package markmath.entities;

public class Question {
    /* Attributes
     * questionNumber: the order number in this assignment
     * fullMark: full mark of this question
     * numberOfErrors
     * finalMark: mark after deduction from errors
     * */

    /* Possible Methods to implement
     * Constructor
     * getFinalMark
     * setFinalMark
     * setErrors
     * */

    private int questionNumber;
    private int numberOfErrors;
    private int finalMark = 0;

    public Question(int No, int error){
        questionNumber = No;
        numberOfErrors = error;
    }

    public int getFinalMark() {return finalMark;}

    public void setFinalMark(int n){
        finalMark = n;
    }

    public void setErrors(int n){numberOfErrors = n;}

}

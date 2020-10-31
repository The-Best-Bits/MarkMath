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
    private int fullMark;
    private int numberOfErrors;
    private int finalMark = 0;

    public Question(int No, int full, int error){
        questionNumber = No;
        fullMark = full;
        numberOfErrors = error;
    }

    public int getFullMark(){return fullMark;}

    public int getFinalMark() {return finalMark;}

    public void setFinalMark(){
        /* finalMark should be calculated by the MarkingCalculator */
        // TODO Implement this with MarkingCalculator
    }

    public void setErrors(int n){numberOfErrors = n;}

}

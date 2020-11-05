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
    private float finalMark = 0;

    public Question(int No, int error){
        questionNumber = No;
        numberOfErrors = error;
    }

    public float getFinalMark() {return finalMark;}

    public int getQuestionNumber(){return questionNumber;}

    public int getNumberOfErrors(){return numberOfErrors;}

    public void setFinalMark(float finalMark){
        this.finalMark = finalMark;
    }

    public void setErrors(int n){numberOfErrors = n;}

}

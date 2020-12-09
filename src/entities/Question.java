package entities;

public class Question {
    /**
     * Following Clean Architecture this is an entity class that represents a single question in an assignment
     * Atributes:
     * questionNumber: number of this question
     * numberOfErrors: the number of errors a student made in their solution for this question
     * finalMark: final mark for this question
     */

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

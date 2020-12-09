package markmath.usecases;

import entities.Question;

public class MarkingCalculator {
    /**
     * Following Clean Architecture this is a use case class that marks a question
     * Attributes:
     * question: Thq question this marking calculator is responsible for marking
     */

    private Question question;

    public MarkingCalculator(Question question){
        this.question = question;
    }

    /**
     * Calculates the final mark for 'question' using the amount of errors of 'question' and the total
     * possible mark for 'question'
     * @param fullMark the total mark this question is out of
     * @return
     */
    public float getMark(float fullMark){
        float calculated = (float) (question.getNumberOfErrors()*0.5);
        return Math.max(fullMark - calculated, 0);
    }
}

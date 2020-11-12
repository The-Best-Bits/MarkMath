package markmath.usecases;

import markmath.entities.Question;

public class MarkingCalculator {

    private Question question;

    public MarkingCalculator(Question question){
        this.question = question;
    }

    public Float getMark(Float fullMark){
        float calculated = (float) (question.getNumberOfErrors()*0.5);
        return fullMark - calculated;
    }
}

package markmath.usecases;

import markmath.entities.Question;

public class MarkingCalculator {

    private Question question;

    public MarkingCalculator(Question question){
        this.question = question;
    }

    public int getMark(){
        int mark = 0;
        //get full mark of the question from the assignment outline
        if (question.getNumberOfErrors() > 1) {
            //implement the method of calculating the mark for a question
        }
        return mark;
    }
}

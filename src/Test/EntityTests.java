package Test;
import markmath.entities.*;
import org.junit.*;

import java.util.HashMap;

public class EntityTests {
    private static StudentAssignment assignment;
    private static Question q1;
    private static Question q2;
    private static AssignmentOutline a1;
    private static Classroom c1;

    @BeforeClass
    public static void setUp(){
        assignment = new StudentAssignment("1", "JasminaBrar",
                "Fractions1", "JasminaBrar-Fractions1" );
        q1 = new Question(1, 3);
        q2 = new Question(2, 0);
        HashMap<String, Float> questionToMarks = new HashMap<>();
        questionToMarks.put("question1", (float) 5.0);
        questionToMarks.put("question2", (float)4);
        a1 = new AssignmentOutline("Fractions1", questionToMarks);
        c1 = new Classroom("Math", "01");

    }

    @AfterClass
    public static void finished(){

    }

    //StudentAssignment Tests
    @Test
    public void modifyQuestionErrors(){
        assignment.addQuestion(q1);
        assignment.modifyQuestionErrors(1, 4);
        Assert.assertEquals(assignment.getQuestions().get(0).getNumberOfErrors(), 4);
    }

    @Test
    public void setFinalMarkSingleQuestion(){
        assignment.addQuestion(q1);
        assignment.setFinalMarkSingleQuestion(1, 5);
        Assert.assertEquals((int)assignment.getQuestions().get(0).getFinalMark(), 5);
    }

    @Test
    public void setFinalMark(){
        assignment.addQuestion(q1);
        assignment.addQuestion(q2);
        assignment.setFinalMarkSingleQuestion(1, 5);
        assignment.setFinalMarkSingleQuestion(2, 6);
        assignment.setFinalMark();
        Assert.assertEquals((int)assignment.getFinalMark(), 11);
    }

    @Test
    public void getQuestion(){
        assignment.addQuestion(q1);
        Assert.assertEquals(assignment.getQuestion(1), q1);
        Assert.assertNull(assignment.getQuestion(2));
    }

    //AssignmentOutline Tests
    @Test
    public void changeQuestionMark(){
        Assert.assertTrue(a1.changeQuestionMark("question1",  1.468f));
        Assert.assertFalse(a1.changeQuestionMark("question3", (float)4));
    }

    @Test
    public void addQuestionMark(){
        Assert.assertTrue(a1.addQuestionMark("question3", (float)3));
        Assert.assertFalse(a1.addQuestionMark("question1", (float)4));
    }

    @Test
    public void returnFullMark(){
        Assert.assertEquals((int)a1.returnFullMark(), 9);
    }

}


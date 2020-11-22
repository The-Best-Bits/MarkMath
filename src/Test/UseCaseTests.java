package Test;

import markmath.entities.AssignmentOutline;
import markmath.entities.Question;
import markmath.entities.StudentAssignment;
import markmath.usecases.MarkingCalculator;
import markmath.usecases.StudentAssignmentManager;
import org.junit.*;

import java.util.HashMap;

public class UseCaseTests {

    private static StudentAssignmentManager manager;
    private static AssignmentOutline outline;
    private static MarkingCalculator calculator;

    @BeforeClass
    public static void setUp(){
        HashMap<String, Integer> finalParsedData = new HashMap<>();
        finalParsedData.put("question1", 6);
        finalParsedData.put("question2", 0);
        finalParsedData.put("question3", 2);
        HashMap<String, Float> questionToMarks = new HashMap<>();
        questionToMarks.put("question1", (float)10);
        questionToMarks.put("question2", (float)5);
        questionToMarks.put("question3", (float)4);
        questionToMarks.put("question4", (float)3);
        outline = new AssignmentOutline("Fractions1", questionToMarks);
        manager = new StudentAssignmentManager("1", "JasminaBrar",
                "JasminaBrar-Fractions1", "Fractions1", finalParsedData, outline);

    }

    @AfterClass
    public static void finished(){

    }

    //StudentAssignmentManager tests
    @Test
    public void addQuestions_customQuestionAdded(){
        StudentAssignment sa1 = manager.getCarbonCopy();
        Assert.assertEquals(sa1.getQuestions().size(), 4);
        Assert.assertEquals(sa1.getQuestion(4).getQuestionNumber(), 4);
        Assert.assertEquals(sa1.getQuestion(4).getNumberOfErrors(), 0);
    }

    @Test
    public void markAllQuestions(){
        manager.markAllQuestions();
        StudentAssignment carbonCopy = manager.getCarbonCopy();
        Assert.assertEquals((int)carbonCopy.getFinalMark(), 18);
        Assert.assertEquals((int)carbonCopy.getQuestion(1).getFinalMark(), 7);
        Assert.assertEquals((int)carbonCopy.getQuestion(2).getFinalMark(), 5);
        Assert.assertEquals((int)carbonCopy.getQuestion(3).getFinalMark(), 3);
        Assert.assertEquals((int)carbonCopy.getQuestion(4).getFinalMark(), 3);
        Assert.assertEquals((int)carbonCopy.getFullMark(), 22);
    }

    //need to test matchBundle

    //MarkingCalculator Tests
    @Test
    public void getMark_questionWithNoErrors(){
        Question q1 = new Question(1, 0);
        calculator = new MarkingCalculator(q1);
        Assert.assertEquals((int)(calculator.getMark((float)5)), 5);

    }

    @Test
    public void getMark_questionWithErrors(){
        Question q1 = new Question(1, 4);
        calculator = new MarkingCalculator(q1);
        Assert.assertEquals((int)(calculator.getMark((float)5)), 3);
    }
}

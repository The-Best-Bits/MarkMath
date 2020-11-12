package Test;

import markmath.entities.AssignmentOutline;
import markmath.entities.StudentAssignment;
import markmath.usecases.StudentAssignmentManager;
import org.junit.*;

import java.util.HashMap;

public class UseCaseTests {

    private static StudentAssignmentManager manager;
    private static AssignmentOutline outline;

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
        manager = new StudentAssignmentManager(1, "JasminaBrar",
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

    //need to test markAllQuestions and matchBundle

}

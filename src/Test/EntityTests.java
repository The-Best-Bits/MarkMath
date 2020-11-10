package Test;
import markmath.entities.*;
import org.junit.*;

import java.util.HashMap;

public class EntityTests {
    private static StudentAssignment assignment;
    private static Question q1;
    private static Question q2;
    private static Student s1;
    private static AssignmentOutline a1;
    private static AssignmentBundle bundle1;
    private static Classroom c1;

    @BeforeClass
    public static void setUp(){
        assignment = new StudentAssignment(1, "JasminaBrar",
                "Fractions1", "JasminaBrar-Fractions1" );
        q1 = new Question(1, 3);
        q2 = new Question(2, 0);
        s1 = new Student(1, "JasminaBrar");
        HashMap<String, Float> questionToMarks = new HashMap<>();
        questionToMarks.put("question1", (float) 5.0);
        questionToMarks.put("question2", (float)4);
        a1 = new AssignmentOutline("Fractions1", questionToMarks);
        bundle1 = new AssignmentBundle(a1);
        c1 = new Classroom("Math");

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

    //Student Tests
    @Test
    public void addAssignments(){
       Assert.assertTrue(s1.addAssignments(assignment));
       Assert.assertFalse(s1.addAssignments(assignment));
    }

    @Test
    public void getAssignment_assignmentInList(){
        s1.addAssignments(assignment);
        try {
            Assert.assertEquals(s1.getAssignment("JasminaBrar-Fractions1"), assignment);
        }catch(NoAssignmentFoundException e)
        {
            System.out.println("No assignment found");
        }
    }

    @Test
    public void getAssignment_assignmentNotInList(){
        try{
            s1.getAssignment("Bob-Fractions1");
        }catch(NoAssignmentFoundException e){
            Assert.assertEquals(1, 1);
        }
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

    //AssignmentBundle Tests
    @Test
    public void getStudentAssignment(){
        bundle1.addStudentAssignment(assignment);
        try{
        Assert.assertEquals(bundle1.getStudentAssignment(1), assignment);
        }catch(NoAssignmentFoundException e){
            System.out.println("No assignment found");
        }

        try
        {
            bundle1.getStudentAssignment(2);
        }catch(NoAssignmentFoundException e)
        {
            Assert.assertEquals(1, 1);
        }
    }

    //Classroom Tests
    @Test
    public void addAssignmentBundle(){
        Assert.assertTrue(c1.addAssignmentBundle(bundle1));
    }

    @Test
    public void enroll(){
        Assert.assertTrue(c1.enroll(s1));
        Assert.assertFalse(c1.enroll(s1));
    }

    @Test
    public void getAssignmentBundle(){
        c1.addAssignmentBundle(bundle1);
        Assert.assertEquals(c1.getAssignmentBundle("Fractions1"), bundle1);
    }


}


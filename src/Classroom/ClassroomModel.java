package Classroom;

import dbUtil.dbConnection;
import markmath.entities.AssignmentBundle;
import markmath.entities.AssignmentOutline;
import markmath.entities.StudentAssignment;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ClassroomModel {
    Connection connection;

    public ClassroomModel(){
        try {
            this.connection = dbConnection.getConnection();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (this.connection == null) {
            System.exit(1);
        }
    }

    public void closeConnection(){
        try {
            this.connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public String getClassroomName(String classID) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT class_name FROM classrooms WHERE class_id = " + classID;

        try {
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql);
            return rs.getString(1);

        } catch (SQLException ex) {
            ex.printStackTrace();
            return ("");
        }
        finally {
            if (stmt != null && rs != null){
                stmt.close();
                rs.close();
            }
        }
    }

    public boolean studentIsInDatabase(String studentID) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM students WHERE student_id = " + studentID;

        try {
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql);
            return rs.next();

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }

        finally {
            if (stmt != null && rs != null){
                stmt.close();
                rs.close();
            }
        }
    }

    public boolean studentIsInClass(String studentID, String classID) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM students WHERE student_id = " + studentID + " AND CHARINDEX(':" + classID + ":', class_id) > 0";

        try {
            if (studentIsInDatabase(studentID)) {
                stmt = this.connection.createStatement();
                rs = stmt.executeQuery(sql);
                return rs.next();
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        finally {
            if (stmt != null && rs != null){
                stmt.close();
                rs.close();
            }
        }
    }

    public boolean addStudentToClass(String studentID, String classID) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            if (studentIsInDatabase(studentID)) {
                String sql1 = "SELECT class_id FROM students where student_id = '" + studentID + "'";
                stmt = this.connection.createStatement();
                rs = stmt.executeQuery(sql1);
                String originalClasses = rs.getString(1);
                String newClasses = originalClasses + classID + ":";

                String sql2 = "UPDATE students SET class_id = '" + newClasses + "' WHERE student_id = " + studentID;
                stmt = this.connection.createStatement();
                stmt.executeUpdate(sql2);
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null){
                rs.close();
            }
        }
    }

    public boolean addStudentToDatabase(String studentID, String studentName) throws Exception {
        Statement stmt = null;

        try {
            if (this.studentIsInDatabase(studentID)) {
                return false;
            } else {
                String sql = "INSERT INTO students (student_id, student_name, class_id) VALUES ('" + studentID + "', '" + studentName + "', ':')";
                stmt = this.connection.createStatement();
                stmt.executeUpdate(sql);
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }

        finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    private void removeStudentFromClass(String studentID, String classID) throws Exception {
        /*removes the class ID associated with the class under the student in the student table in the database.
        If the student is enrolled in no classes after this, the student is removed from the Database.*/
        if (!studentIsInClass(studentID, classID)) {
            return;
        }

        Statement stmt = null;
        ResultSet rs = null;

        try {
            String sql1 = "SELECT class_id FROM students where student_id = '" + studentID + "'";
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql1);

            String originalClasses = rs.getString(1);
            String newClasses = originalClasses.replace(classID + ":", "");

            String sql2;

            if (newClasses.trim().equals(":")) {
                sql2 = "DELETE FROM students WHERE student_id = '" + studentID + "'";
            } else {
                sql2 = "UPDATE students SET class_id = '" + newClasses + "' WHERE student_id = '" + studentID + "'";
            }

            stmt.executeUpdate(sql2);

        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    private void removeStudentFromAssignment(String studentID, String assignmentID) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            DatabaseMetaData dbm = connection.getMetaData();
            rs = dbm.getTables(null, null, assignmentID, null);
            if (rs.next()) {
                String sql = "DELETE FROM '" + assignmentID + "' WHERE student_id = '" + studentID + "'";
                stmt = this.connection.createStatement();
                stmt.executeUpdate(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    private ArrayList<String> getAssignmentsInClass(String classID) throws Exception{
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<String> assignmentsInClass = new ArrayList<>();

        try {
            String sql = "SELECT assignmentbundle_id FROM AssignmentBundles WHERE classroom_id = '" + classID + "'";
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                assignmentsInClass.add(rs.getString(1));
            }

            return assignmentsInClass;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    public void removeStudent(String studentID, String classID) throws Exception {
        /* removes the class ID associated with the class under the student in the student table in the database.
        If the student is enrolled in no classes after this, the student is removed from the Database.
        Also removes students from all assignment bundle tables.
         */
        if (!studentIsInClass(studentID, classID)) {
            return;
        }

        try {
            this.removeStudentFromClass(studentID, classID);

            ArrayList<String> assignmentsInClass = this.getAssignmentsInClass(classID);

            for (String assignment: assignmentsInClass) {
                this.removeStudentFromAssignment(studentID, assignment);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public void createAssignmentTable(String received_id, AssignmentBundle assignment) throws Exception {
        Statement stmt = null;

        StringBuilder outline = new StringBuilder();
        int numOfQuestions = assignment.getOutline().getNumberOfQuestions();
        int i = 0;
        while (i != numOfQuestions) {
            outline.append("question").append(i+1).append(" REAL, ");
            i = i+1;
        }

        String outlineInDatabase = outline.toString();
        String sql = "CREATE TABLE IF NOT EXISTS " + "[" + received_id + "]" + " (student_id STRING, student_name STRING, " +
                "document_name STRING, "+ outlineInDatabase + "total STRING);";

        /*String sql = "CREATE TABLE IF NOT EXISTS" + assignment.getName() +" (\n"
                + "     bundle_id string PRIMARY KEY,\n"
                + " 	student_id string,\n"
                + " 	student_name string,\n"
                + " 	document_name string,\n"
                + " 	capacity real\n"
                + ");"; */
        try {
            stmt = this.connection.createStatement();
            stmt.execute(sql);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            if (stmt != null ){
                stmt.close();

            }
        }
        //Adds outline as student 0 to indicate the full marks

    }

    public void addOutlineToAssignmentTable(AssignmentOutline outline, String assignmentID){

        StringBuilder outlineInDatabase = new StringBuilder();
        int numOfQuestions = outline.getNumberOfQuestions();
        HashMap<String, Float> outlineMap = outline.getQuestionToMarks();
        int i = 0;
        while (i != numOfQuestions-1) {
            outlineInDatabase.append("question").append(i+1).append(", ");
            i = i+1;
        }
        outlineInDatabase.append("question").append(i+1);
        //System.out.println("you tried adding the outline");
        //System.out.println(outlineInDatabase);
        //System.out.println(String.join("", Collections.nCopies(numOfQuestions, "?")));

        String sqlInsert = "INSERT INTO " + "[" + assignmentID + "]"+" (student_id, student_name, document_name, " + outlineInDatabase.toString() +
                ", total) VALUES (?, ?, ?, " + String.join("", Collections.nCopies(numOfQuestions, "?, ")) + "?)";
        //System.out.println(sql);
        try {
            System.out.println("you are in the try loop");
            PreparedStatement stmt = connection.prepareStatement(sqlInsert);
            stmt.setString(1, "0");
            stmt.setString(2,"outline");
            stmt.setString(3, outline.getName());
            i = 0;
            while (i != numOfQuestions) {
                stmt.setFloat(4+i, outlineMap.get("question" +(i+1)));
                i = i+1;
            }
            stmt.setFloat(4+i, outline.returnFullMark());
            boolean success = stmt.execute();
            System.out.println(success);
            stmt.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        }

    /**
     * Helper function to markStudentAssignment. Differs from assignmentBundleInClassroom as this method checks if
     * there exists an assignmentbundle with the given name in this classroom. Because assignmentbundle names are unique
     * in a classroom this is a valid approach to search if an assignmentBundle is in a classroom. When marking a Hypatia
     * document we do not have the assignment bundle ID, we only have the assignment bundle name.
     * @param assignmentType Name of the assignmentbundle
     * @param classroomID  ID of the classroom
     * @return True iff this classroom contains an assignmentbundle with this name
     */
    public boolean assignmentBundleNameInClassroom(String assignmentType, String classroomID) throws SQLException{

        ResultSet rs = null;

        try{
            String sqlQuery = "SELECT assignment_name FROM AssignmentBundles WHERE classroom_id =" + classroomID;
            rs = this.connection.createStatement().executeQuery(sqlQuery);
            while (rs.next()){
                if(rs.getString("assignment_name").equals(assignmentType)){
                    return true;
                }
            }
            return false;

        }catch(SQLException e){
            System.out.println("Assignment bundle name in classroom");
            System.out.println("Error" + e);
        }

        finally {
            if(rs != null) {
                rs.close();
            }
        }

        return false;
    }


    /**
     * @param assignmentType Name of the assignmentbundle
     * @param classroomID ID of the classroom this assignmentbundle is in
     * @return the assignmentoutline of the assignmentbundle with the give name
     */
    public AssignmentOutline getAssignmentOutline(String assignmentType, String classroomID) throws SQLException{

        ResultSet rs = null;

        try{
            String assignmentBundleID = getIDOfAssignmentBundle(assignmentType, classroomID);
            String sqlQuery = "SELECT * FROM '" + assignmentBundleID + "' ORDER BY ROWID ASC LIMIT 1";
            System.out.println(sqlQuery);
            rs = this.connection.createStatement().executeQuery(sqlQuery);
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumns = rsmd.getColumnCount();
            HashMap<String, Float> questionToMarks = new HashMap<>();
            System.out.println(numColumns);
            for (int i =1; i <= numColumns-4; i++){
                String question = "question" + i;
                questionToMarks.put(question, rs.getFloat(question));
            }
            AssignmentOutline outline = new AssignmentOutline(assignmentType, questionToMarks);
            return outline;
        }catch(SQLException e){
            System.out.println("Get Assignment Outline");
            System.out.println("Error" + e);
        }

        finally {
            if(rs != null){
                rs.close();
            }
        }
        return null;
    }

    /**
     * Gets the ID of the assignment bundle with name 'assignmentBundleName' in the classroom with ID 'classroomID'
     * @param assignmentBundleName name of the assignment bundle
     * @param classroomID ID of the classroom this assignment bundle is in
     * @return ID of the assignmentbundle with the given name in this classroom
     * Note: There is only ever one assignmentbundle with a given name in a classroom, so there can only be one ID
     * returned
     */
    public String getIDOfAssignmentBundle(String assignmentBundleName, String classroomID) throws SQLException{

        ResultSet rs = null;

        try{

            String sql = "SELECT assignmentbundle_id FROM AssignmentBundles WHERE assignment_name = '"
                    + assignmentBundleName + "'"+ " AND classroom_id ='" + classroomID + "'";
            System.out.println(sql);
            rs = this.connection.createStatement().executeQuery(sql);
            System.out.println(rs.next());
            System.out.println(classroomID);
            String result = rs.getString("assignmentbundle_id");
            System.out.println(result);
            return result;

        }catch(SQLException e){
            System.out.println("Get Assignment bundle ID");
            System.out.println("Error" + e);
        }

        finally {
            if(rs != null){
                rs.close();
            }
        }
        return null;
    }

    /**
     * Gets the name of the student with the given studentID 'studentNum'
     * @param studentNum student ID
     * @return name of the student with the given student ID
     */
    public String getStudentNameFromDatabase(String studentNum) throws SQLException{

        ResultSet rs = null;

        try{
            String sqlQuery = "SELECT student_name FROM students WHERE student_id = " + studentNum;
            rs = this.connection.createStatement().executeQuery(sqlQuery);
            String studentName = rs.getString("student_name");
            return studentName;
        }
        catch(SQLException e){
            System.out.println("Error" + e);
        }

        finally {
            if(rs != null){
                rs.close();
            }
        }
        return null;
    }

    /**
     * Adds a student's assignment to the database in the assignmentbundle table this student assignment belongs in.
     * @param sqlInsert SQL Query to be executed
     * @param assignment Student's Assignment
     * @param inDatabase boolean representing whether this student assignment already exists in the corresponding
     *                   assignment bundle table
     */
    public void addStudentAssignmentToDatabaseModel(String sqlInsert, StudentAssignment assignment, boolean inDatabase) throws SQLException{

        PreparedStatement stmt = null;

        try{
            stmt = this.connection.prepareStatement(sqlInsert);
            if(!inDatabase) {
                stmt.setString(1, assignment.getStudentID());
                stmt.setString(2, assignment.getStudentName());
                stmt.setString(3, assignment.getAssignmentName());
                int q = 1;
                int numQuestions = assignment.getQuestions().size();
                while (q <= numQuestions) {
                    stmt.setFloat(q + 3, assignment.getQuestion(q).getFinalMark());
                    q += 1;
                }
                stmt.setFloat(q + 3, assignment.getFinalMark());
            }
            stmt.execute();

        }catch(SQLException e){
            System.out.println("Add Student assignment to database");
            System.out.println("Error" + e);
        }

        finally {
            if(stmt != null){
                stmt.close();
            }
        }
    }

    /**
     * Checks if there already exists an assignment for a student with ID 'studentNum' in the database in the table
     * for the given assignment bundle 'assignmentType'
     * @param studentNum StudentID of a student
     * @param assignmentType Name of an assignmentbundle in this classroom
     * @return True iff there already exists an assignment in the given assignment bundle in this classroom for a
     * student with the given studentID
     */
    public boolean studentAssignmentInDatabase(String studentNum, String assignmentType, String classroomID) throws SQLException{

        ResultSet rs = null;

        try{
            String assignmentbundleID = getIDOfAssignmentBundle(assignmentType, classroomID);
            String sqlQuery = "SELECT student_name FROM '" + assignmentbundleID + "' WHERE student_id =" + studentNum;
            rs = this.connection.createStatement().executeQuery(sqlQuery);
            return rs.next();
        }catch(SQLException e){
            System.out.println("Error" + e);
        }

        finally{
            if(rs != null){
                rs.close();
            }
        }
        return false;
    }
}




